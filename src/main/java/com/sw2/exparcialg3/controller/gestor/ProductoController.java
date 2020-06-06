package com.sw2.exparcialg3.controller.gestor;


import com.sw2.exparcialg3.entity.Producto;
import com.sw2.exparcialg3.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/gestor/productos")
public class ProductoController {

    @Autowired
    ProductoRepository productoRepository;


    @GetMapping("/new")
    public String nuevoProductoFrm(@ModelAttribute("producto") Producto producto,
                                   Model model) {

        return "gestor/nuevoFrm";
    }

    @PostMapping("/save")
    public String guardarProducto(@ModelAttribute("producto") Producto producto,
                                  RedirectAttributes attr) {
        if (producto.getCodigo() == null) {
            attr.addFlashAttribute("msg", "Producto creado exitosamente");
        } else {
            attr.addFlashAttribute("msg", "Producto actualizado exitosamente");
        }
        productoRepository.save(producto);
        return "redirect:/gestor/productos";
    }

    @GetMapping("/edit")
    public String editarProducto(@ModelAttribute("producto") Producto producto,
                                      Model model, @RequestParam("id") String id) {

        Optional<Producto> optProduct = productoRepository.findById(id);
        if (optProduct.isPresent()) {
            producto = optProduct.get();
            model.addAttribute("product", producto);
            return "producto/nuevoFrm";
        } else {
            return "redirect:/gestor/productos";
        }

    }

    @GetMapping("/delete")
    public String borrarProducto(Model model,
                                      @RequestParam("id") String id,
                                      RedirectAttributes attr) {

        Optional<Producto> optProduct = productoRepository.findById(id);

        if (optProduct.isPresent()) {
            productoRepository.deleteById(id);
            attr.addFlashAttribute("msg", "Producto borrado exitosamente");
        }
        return "redirect:/gestor/productos";

    }

}
