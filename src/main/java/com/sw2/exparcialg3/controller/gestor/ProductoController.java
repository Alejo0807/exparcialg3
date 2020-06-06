package com.sw2.exparcialg3.controller.gestor;


import com.sw2.exparcialg3.entity.Producto;
import com.sw2.exparcialg3.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/gestor/productos")
public class ProductoController {
    // Crud de productos hecho por el gestor

    @Autowired
    ProductoRepository productoRepository;

    @GetMapping(value ={"/",""})
    public String lista(
                                   Model model) {
        model.addAttribute("productos",productoRepository.findAll());
        return "gestor/listaProductoGestor";
    }

    @GetMapping("/new")
    public String nuevoProductoFrm(@ModelAttribute("producto") Producto producto,
                                   Model model) {
        model.addAttribute("formtype", "1");
        return "gestor/formProducto";
    }

    @GetMapping("/edit")
    public String editarProducto(@ModelAttribute("producto") Producto producto,
                                      Model model, @RequestParam("id") String id) {

        model.addAttribute("formtype", "0");
        Optional<Producto> optProduct = productoRepository.findById(id);
        if (optProduct.isPresent()) {
            producto = optProduct.get();
            model.addAttribute("producto", producto);
            return "gestor/formProducto";
        } else {
            return "redirect:/gestor/productos";
        }

    }

    @PostMapping("/save")
    public String guardarProducto(@ModelAttribute("producto") @Valid  Producto producto, BindingResult bindingResult,
                                  @RequestParam("type") int type,
                                  RedirectAttributes attr, Model model) {
        if(type==1) {
            Float f = producto.getPrecio();
            if(Float.toString(f.intValue()).length() >4 || Float.toString(f.floatValue()).length() > 2  || f<0 || f==null){
                bindingResult.rejectValue("precio", "error.user", "El precio debe tener 2 decimales y 4 cifras enteras");
            }
            bindingResult.rejectValue("foto", "error.user", "La foto no debe estar vacía");
            if (productoRepository.findById(producto.getCodigo()).isPresent()) { //if new
                bindingResult.rejectValue("codigo", "error.user", "Este dni ya existe");
            }
        }

        if(bindingResult.hasErrors()){
            model.addAttribute("formtype",Integer.toString(type));
            return "gestor/formProducto";
        }
        else {

            Optional<Producto> optionalUsuario = productoRepository.findById(producto.getCodigo());
            if (optionalUsuario.isPresent()) {
                productoRepository.update_producto(producto.getCodigo(),
                        producto.getNombre(), producto.getDescripcion(), producto.getStock());
                attr.addFlashAttribute("msgSuccess", "Gestor actualizado exitosamente");
            }
            else {
                attr.addFlashAttribute("msgSuccess", "Gestor creado exitosamente");
                productoRepository.save(producto);
            }

        }
        return "redirect:/gestor/productos";
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
