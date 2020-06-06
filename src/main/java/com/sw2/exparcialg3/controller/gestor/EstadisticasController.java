package com.sw2.exparcialg3.controller.gestor;


import com.sw2.exparcialg3.entity.Producto;
import com.sw2.exparcialg3.repository.ProductoRepository;
import com.sw2.exparcialg3.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/gestor/e")
public class EstadisticasController {

    @Autowired
    ProductoRepository productoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping(value = {"","/"})
    public String easfte(Model model, HttpSession session){
        Optional<Producto> opt = productoRepository.findById("20120537");

        if (opt.isPresent()){
            model.addAttribute("productomasvendido", opt.get());
            model.addAttribute("productomenosvendido", opt.get());
            model.addAttribute("productomascaro", opt.get());
            model.addAttribute("usuariomas", session.getAttribute("usuario"));
        }

        return "gestor/estad";
    }



}
