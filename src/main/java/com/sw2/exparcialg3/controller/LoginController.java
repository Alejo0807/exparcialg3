package com.sw2.exparcialg3.controller;


import com.sw2.exparcialg3.entity.Pedido;
import com.sw2.exparcialg3.entity.Rol;
import com.sw2.exparcialg3.entity.Usuario;
import com.sw2.exparcialg3.repository.PedidoRepository;
import com.sw2.exparcialg3.repository.RolRepository;
import com.sw2.exparcialg3.repository.UsuarioRepository;
import com.sw2.exparcialg3.utils.CustomMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

@Controller
public class LoginController {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    PedidoRepository pedidoRepository;
    @Autowired
    RolRepository rolRepository;
    @Autowired
    CustomMailService customMailService;

    @GetMapping(value = {"/loginForm"})
    public String login(){

        return "login";
    }

    @GetMapping(value = {"/"})
    public String loginasfa(){

        return "redirect:/productos";
    }

    @GetMapping("/redirectByRole")
    public String redirectByRole(Authentication auth, HttpSession session) {
        String rol = "";
        for (GrantedAuthority role : auth.getAuthorities()) {
            rol = role.getAuthority();
            break;
        }

        Usuario usuarioLogueado = usuarioRepository.findByCorreo(auth.getName());
        session.setAttribute("usuario", usuarioLogueado);

        switch (rol) {
            case "registrado":
                session.setAttribute("carrito",
                        new Pedido());
                return "redirect:/productos";
            case "gestor":
                return "redirect:/gestor";
            case "admin":
                return "redirect:/admin";
        }
        return "/";

    }

    @GetMapping("/forgotpassword")
    public String RecuperarContrasena(){return "forgot-password";}

    @PostMapping("/processForgotPassword")
    public String processForgotPassword(Model model, @RequestParam(value = "username", required = true) String email,
                                        RedirectAttributes attr) throws IOException, MessagingException {
        System.out.println(email);
        Usuario u = usuarioRepository.findByCorreo(email);
        customMailService.sendEmail(email,
                "Recuperación de contraseña", "Nueva contraseña",
                "Su nueva contraseña es: \n"
                        +u.generateNewPassword());
        usuarioRepository.save(u);
        attr.addFlashAttribute("msg","Se le ha enviado a su correo electrónico su nueva contraseña");
        return "redirect:/loginForm";
    }

    @GetMapping("/signup")
    public String nuevoUsuario(@ModelAttribute("usuario") Usuario u) {

        return "register";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute("usuario") @Valid Usuario u,
                                 BindingResult bindingResult, @RequestParam("pass") String pass,
                                 RedirectAttributes attr, Model model) {
        System.out.println(pass);
        System.out.println(u.getPassword());
        String pattern = "^(?=(?:\\D*\\d){2})[a-zA-Z0-9]*$";
        String s = u.getCorreo().replace(" ","");

        if(usuarioRepository.findById(u.getDni()).isPresent()){ //if new
            bindingResult.rejectValue("dni","error.user","Este dni ya existe");
        }

        if(usuarioRepository.findByCorreo(u.getCorreo()) != null){
            bindingResult.rejectValue("correo","error.user","Este correo ya está en uso");
        }

        if(!(u.getPassword().equals(pass))){
            bindingResult.rejectValue("password","error.user","Las contraseñas deben de ser idénticas");
        }

        if(!u.validatePassword()){
            bindingResult.rejectValue("password","error.user","La contraseña debe tener entre 8 y 10 caracteres y como mínimo 2 números");
        }

        if(!(s.endsWith("@pucp.edu.pe") || s.endsWith("@pucp.pe"))){
            bindingResult.rejectValue("correo","error.user","El correo debe ser pucp.edu.pe o pucp.pe");
        }

        if(bindingResult.hasErrors()){
            model.addAttribute("msgError", "ERROR");
            return "/register";
        }else{
            attr.addFlashAttribute("msg", "Usuario creado exitosamente");
            usuarioRepository.save_usuario(u.getDni(), u.getNombre(), u.getApellido(), u.getCorreo(), u.getPassword());
            Pedido p = new Pedido("carrito_"+Integer.toString(u.getDni()));
            p.setComprado(0);
            p.setUsuario(u);
            pedidoRepository.save(p);
            return "redirect:/loginForm";
        }

    }
}
