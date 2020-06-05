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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping("/realizarCompra")
    public String Comprar(@RequestParam("cod_ped") Pedido ped,
                          @RequestParam("cod_prod") Producto prod){

        Optional<PedidoHasProducto> php = pedidoHasProductoRepository.findById(new PedProdId(ped,prod));
        if(php.isPresent()){

        }

        return "";
    }


    @GetMapping("/pedidos")
    public String Pedidos(Model model){
        model.addAttribute("pedidos", pedidoRepository.findAll());
        return "";
    }
}
