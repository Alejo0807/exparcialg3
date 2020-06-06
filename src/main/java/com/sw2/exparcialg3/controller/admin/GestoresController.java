package com.sw2.exparcialg3.controller.admin;

import com.sw2.exparcialg3.entity.Usuario;
import com.sw2.exparcialg3.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

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

    @GetMapping("/edit")
    public  String asdnadsin(@RequestParam("id") int dni, @ModelAttribute("usuario") Usuario usuario, Model model){

        Optional<Usuario> optionalUsuario = usuarioRepository.findUsuarioByDniAndRol_Idrol(dni, ROL_CRUD);
        if(optionalUsuario.isPresent()){
            usuario = optionalUsuario.get();
            model.addAttribute("usuario", usuario);

            return "admin/formGestores";
        }
        return "redirect:/admin/gestores";
    }

    @GetMapping("/new")
    public  String asdnadsin(@ModelAttribute("usuario") Usuario usuario, Model model){
            return "admin/formGestores";
    }


    @PostMapping("/save")
    public  String asdnaasdasddsin(@ModelAttribute("usuario") @Valid Usuario usuario,
                                   BindingResult bindingResult, Model model){

        Optional<Usuario> optionalUsuario = usuarioRepository.findUsuarioByDniAndRol_Idrol(1, ROL_CRUD);
        if(optionalUsuario.isPresent()){


            return "admin/formGestores";
        }
        return "redirect:/admin/gestores";
    }


    @GetMapping("/delete")
    public  String asdnadsin(@RequestParam("id") int dni, RedirectAttributes attr){

        Optional<Usuario> optionalUsuario = usuarioRepository.findUsuarioByDniAndRol_Idrol(dni, ROL_CRUD);
        if(optionalUsuario.isPresent()){
            Usuario usuario = optionalUsuario.get();
            usuarioRepository.delete(usuario);
            attr.addFlashAttribute("msgSuccess","Gestor borrado exitosamente");
            return "redirect:/admin/gestores";
        }
        return "redirect:/admin/gestores";
    }


}
