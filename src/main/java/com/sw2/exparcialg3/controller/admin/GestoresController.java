package com.sw2.exparcialg3.controller.admin;

import com.sw2.exparcialg3.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/gestores")
public class GestoresController {

    @Autowired
    UsuarioRepository usuarioRepository;

    private final int ROL_CRUD = 2; // rol al que se le hace crud;

    @GetMapping(value = {""})
    public String lista(Model model){
        model.addAttribute("lista", usuarioRepository.findUsuariosByRol_Idrol(ROL_CRUD));
        return "admin/listaGestores";
    }


}
