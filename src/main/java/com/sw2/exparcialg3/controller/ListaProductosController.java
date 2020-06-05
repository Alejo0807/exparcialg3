package com.sw2.exparcialg3.controller;

import com.sw2.exparcialg3.repository.PedidoRepository;
import com.sw2.exparcialg3.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ListaProductos")
public class ListaProductosController {

    @Autowired
    ProductoRepository productoRepository;
    @Autowired
    PedidoRepository pedidoRepository;

    @GetMapping(value = {"", "/"})
    public String Lista(Model model){
        model.addAttribute("productos", productoRepository.findAll());
        return "";
    }

}
