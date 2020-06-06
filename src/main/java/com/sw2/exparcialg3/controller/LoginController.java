package com.sw2.exparcialg3.controller;


import com.sw2.exparcialg3.entity.Usuario;
import com.sw2.exparcialg3.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

}
