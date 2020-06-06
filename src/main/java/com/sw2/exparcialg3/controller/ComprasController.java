package com.sw2.exparcialg3.controller;


import com.sw2.exparcialg3.constantes.PedProdId;
import com.sw2.exparcialg3.entity.Pedido;
import com.sw2.exparcialg3.entity.PedidoHasProducto;
import com.sw2.exparcialg3.entity.Producto;
import com.sw2.exparcialg3.repository.PedidoHasProductoRepository;
import com.sw2.exparcialg3.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/compras")
public class ComprasController {

    @Autowired
    PedidoRepository pedidoRepository;
    @Autowired
    PedidoHasProductoRepository pedidoHasProductoRepository;

    @GetMapping("/carrito")
    public String Carrito(Model model){
        model.addAttribute("carrito", pedidoHasProductoRepository.findAll());
        return "";
    }

    @GetMapping("/checkout")
    public String Comprar(@ModelAttribute("pedido") Pedido pedido/*, @RequestParam("cod_ped") Pedido ped*/,Model model){


     //   ped.getListPedidoHasProductos();

        return "pedido/checkout";
    }

    @PostMapping("/guardarCompraEnHistorial")
    public String GuardarCompra(@RequestParam("cod_ped") Pedido ped, Model model){


        ped.getListPedidoHasProductos();

        return "redirect:/ListaProductos/";
    }




    @GetMapping("/pedidos")
    public String Pedidos(Model model){

        model.addAttribute("pedidos", pedidoRepository.findAll());
        return "";
    }
}
