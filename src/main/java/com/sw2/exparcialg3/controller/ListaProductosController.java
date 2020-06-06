package com.sw2.exparcialg3.controller;

import com.sw2.exparcialg3.constantes.PedProdId;
import com.sw2.exparcialg3.entity.Pedido;
import com.sw2.exparcialg3.entity.PedidoHasProducto;
import com.sw2.exparcialg3.entity.Producto;
import com.sw2.exparcialg3.entity.Usuario;
import com.sw2.exparcialg3.repository.PedidoHasProductoRepository;
import com.sw2.exparcialg3.repository.PedidoRepository;
import com.sw2.exparcialg3.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ListaProductosController {

    @Autowired
    ProductoRepository productoRepository;
    @Autowired
    PedidoRepository pedidoRepository;
    @Autowired
    PedidoHasProductoRepository pedidoHasProductoRepository;


    @GetMapping(value = {"", "/"})
    public String Lista(Model model){

        model.addAttribute("productos", productoRepository.findByStockIsGreaterThan(0));
        return "producto/listaProducto";
    }

    @GetMapping("/ver")
    public String VerMas(@RequestParam("id") String cod, Model model){

        Optional<Producto> optProd = productoRepository.findById(cod);
        if(optProd.isPresent()){
            Producto prod = optProd.get();
            model.addAttribute("producto", prod);
        }
        return "producto/verProducto";
    }

    @GetMapping("/u/agregarAlCarrito")
    public String AgregarCarrito(@RequestParam(name = "id") String cod, HttpSession session){

        //System.out.println("hola");
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<Producto> optProd = productoRepository.findById(cod);
        Optional<Pedido> optPed = pedidoRepository.findById("carrito_"+ Integer.toString(usuario.getDni()));


        if(optProd.isPresent()){
            Producto prod = optProd.get();
            if(optPed.isPresent()){
                Pedido ped = optPed.get();
                PedidoHasProducto php = new PedidoHasProducto(new PedProdId(ped, prod), 1);
                //guardar de otra manera
                pedidoHasProductoRepository.save(php);
            }
        }

        return "redirect:/productos";
    }

}
