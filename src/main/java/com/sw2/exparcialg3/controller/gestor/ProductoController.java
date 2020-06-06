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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
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
                                  @RequestParam("type") int type, RedirectAttributes attr, Model model,
                                  @RequestParam(value = "photo", required = false) MultipartFile multipartFile) {
        String filename;
        if(type==1) {
            //validaciones manuales del precio y foto
            String[] ff = producto.getPrecio().toString().split("\\.");
            System.out.println(ff.length);

            if(ff[0].length()>2 || ff[1].length()>2){
                bindingResult.rejectValue("precio", "error.user", "El precio debe tener 2 decimales y 2 cifras enteras");
            }
            if (multipartFile.isEmpty()) {
                bindingResult.rejectValue("foto", "error.user", "La foto no debe estar vacía");
            }
            else
            {
                filename= multipartFile.getOriginalFilename();
                System.out.println(filename);
                if ((filename==null) || filename.contains("..") || !(filename.contains(".jpg") || filename.contains(".png") || filename.contains(".jpeg"))){
                    bindingResult.rejectValue("foto", "error.user", "Solo se permite formatos png, jpg y jpeg");
                }
            }
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
            if (optionalUsuario.isPresent()&&type==0) {
                productoRepository.update_producto(producto.getCodigo(),
                        producto.getNombre(), producto.getDescripcion(), producto.getStock());
                attr.addFlashAttribute("msgSuccess", "Gestor actualizado exitosamente");
            }
            else if (type==1){

                try{
                    producto.setFoto(multipartFile.getBytes());
                    producto.setFotocontenttype(multipartFile.getContentType());
                    productoRepository.save(producto);
                    attr.addFlashAttribute("msgSuccess", "Gestor creado exitosamente");
                } catch (IOException e) {
                    e.printStackTrace();
                    attr.addFlashAttribute("msgSuccess", "Ocurrió un error en la subida del archivo");
                }

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
            attr.addFlashAttribute("msgSuccess", "Producto borrado exitosamente");
        }
        return "redirect:/gestor/productos";
    }

}
