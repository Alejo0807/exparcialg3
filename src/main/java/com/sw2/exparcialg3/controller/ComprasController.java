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

import javax.validation.constraints.Null;
import java.lang.reflect.Array;
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
    public String Comprar(@ModelAttribute("pedido") Pedido pedido,Model model){


     //   ped.getListPedidoHasProductos();

        return "pedido/checkout";
    }

    @PostMapping("/pagarYguardarCompra")
    public String GuardarCompra(@RequestParam("creditCard") String creditCard, Model model){

        String[] arrOfStr = creditCard.split("(?<=[0-9])");
        System.out.println(arrOfStr[1]);
        int[] intArray = new int[16];
//4556628646488641
        for (int i = 0; i < 16; i++) {
            intArray[i] = Integer.parseInt(arrOfStr[i]);
        }

        for (int i = 0; i < 15; i=i+2){
            intArray[i] = intArray[i] * 2;
        }


        for (int i = 0; i < 15; i++){
            if (intArray[i] > 9) {
                intArray[i] = intArray[i] - 9;
            }
         }

        int sum = 0;

        for (int i = 0; i < 15; i++) {
            sum = sum + intArray[i];
        }

        int verifier = (10 - (sum % 10)) % 10;
        System.out.println(verifier);
        System.out.println(sum);
        if (verifier == intArray[15]){
            return "redirect:/productos/";
        }else{
            return "redirect:/compras/checkout";
        }
    }




    @GetMapping("/pedidos")
    public String Pedidos(Model model){

        model.addAttribute("pedidos", pedidoRepository.findAll());
        return "";
    }
}
