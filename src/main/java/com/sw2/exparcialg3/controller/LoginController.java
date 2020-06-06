package com.sw2.exparcialg3.controller;


import com.sw2.exparcialg3.entity.Pedido;
import com.sw2.exparcialg3.entity.Rol;
import com.sw2.exparcialg3.entity.Usuario;
import com.sw2.exparcialg3.repository.PedidoRepository;
import com.sw2.exparcialg3.repository.RolRepository;
import com.sw2.exparcialg3.repository.UsuarioRepository;
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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    PedidoRepository pedidoRepository;
    @Autowired
    RolRepository rolRepository;

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
                return "redirect:/pedido/listaPedidos";
            case "gestor":
                return "redirect:/gestor";
            case "admin":
                return "redirect:/admin";
        }
        return "/";

    }

    @GetMapping("/forgotpassword")
    public String RecuperarContraseña(){return "forgot-password";}

    @PostMapping("/processForgotPassword")
    public String processForgotPassword(Model model, @RequestParam(value = "username", required = true) String email,
                                        RedirectAttributes attr){
        System.out.println(email);
        model.addAttribute("msg", email);
        attr.addFlashAttribute("Se le ha enviado a su correo electrónico su nueva contraseña");
        return "redirect:/forgotpassword";
    }

    @GetMapping("/signup")
    public String nuevoUsuario(@ModelAttribute("usuario") Usuario u) {
        return "/register";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute("usuario") @Valid Usuario u,
                                 BindingResult bindingResult,
                                 RedirectAttributes attr, Model model) {
        System.out.println(u.getDni());

        if(usuarioRepository.findById(u.getDni()).isPresent()){ //if new
            bindingResult.rejectValue("dni","error.user","Este dni ya existe");
        }else if(usuarioRepository.findByCorreo(u.getCorreo()).isEnable()){
            bindingResult.rejectValue("correo","error.user","Este correo ya está en uso");
        }

        String s = u.getCorreo().replace(" ","");
        if(!(s.endsWith("@pucp.edu.pe") || s.endsWith("@pucp.pe"))){
            bindingResult.rejectValue("correo","error.user","El correo debe ser pucp.edu.pe o pucp.pe");
        }

        if(bindingResult.hasErrors()){
            model.addAttribute("msgError", "ERROR");
            return "/register";
        }else{
            u.setEnable(true);
            u.setRol(rolRepository.findById(3));
            attr.addFlashAttribute("msg", "Usuario creado exitosamente");
            usuarioRepository.save(u);
            Pedido p = new Pedido("carrito_"+Integer.toString(u.getDni()));
            p.setComprado(0);
            p.setUsuario(u);
            pedidoRepository.save(p);
            return "redirect:/loginForm";
        }

    }
}
