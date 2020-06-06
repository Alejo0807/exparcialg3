package com.sw2.exparcialg3.controller;


import com.sw2.exparcialg3.entity.Rol;
import com.sw2.exparcialg3.entity.Usuario;
import com.sw2.exparcialg3.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    UsuarioRepository usuarioRepository;

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
    public String guardarShipper(@ModelAttribute("usuario") Usuario u,
                                 RedirectAttributes attr) {
        u.setEnable(true);
        u.setRol(new Rol("registrado"));
        attr.addFlashAttribute("msg", "Usuario creado exitosamente");
        usuarioRepository.save(u);
        return "redirect:/loginForm";
    }
}
