package com.sw2.exparcialg3.controller.gestor;


import com.sw2.exparcialg3.dto.*;
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
        GetCantidadCompras gcc = productoRepository.getGetCantidadCompras();
        TotalFacturado tf = productoRepository.getTotalFacturado();
        CantidadProductosVendidos cpv = productoRepository.getCantidadProductosVendidos();
        ProductoMasVendidoDto pmasv = productoRepository.getProdcutoMasVendidoDto();
        ProductoMasVendidoDto pmenosv = productoRepository.getProdcutoMenosVendidoDto();
        ProductoMasCaro pmc = productoRepository.getProductoMasCaro();
        UsuarioQueMasGasto umg = productoRepository.getUsuarioQueMasGasto();


        model.addAttribute("gcc", gcc.getCantidad());
        model.addAttribute("tf", tf.getTotal());
        model.addAttribute("cpv", cpv.getCantidad());

        model.addAttribute("productomasvendido", productoRepository.findById(pmasv.getProducto()).orElse(null));
        model.addAttribute("productomenosvendido", productoRepository.findById(pmenosv.getProducto()).orElse(null));
        model.addAttribute("productomascaro", productoRepository.findById(pmc.getProducto()).orElse(null));
        model.addAttribute("usuariomas", usuarioRepository.findById(umg.getUsuario()).orElse(null));

        return "gestor/estad";
    }



}
