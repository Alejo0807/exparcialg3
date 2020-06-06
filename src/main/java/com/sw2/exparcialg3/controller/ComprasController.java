package com.sw2.exparcialg3.controller;


import com.sw2.exparcialg3.constantes.PedProdId;
import com.sw2.exparcialg3.entity.Pedido;
import com.sw2.exparcialg3.entity.PedidoHasProducto;
import com.sw2.exparcialg3.entity.Producto;
import com.sw2.exparcialg3.entity.Usuario;
import com.sw2.exparcialg3.repository.PedidoHasProductoRepository;
import com.sw2.exparcialg3.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/u")
public class ComprasController {

    @Autowired
    PedidoRepository pedidoRepository;
    @Autowired
    PedidoHasProductoRepository pedidoHasProductoRepository;

    @GetMapping("/carrito")
    public String Carrito(Model model, HttpSession session){

        Usuario u = (Usuario) session.getAttribute("usuario");
        Pedido ped = new Pedido("carrito_" + Integer.toString(u.getDni()));
        model.addAttribute("carrito", ped.getListPedidoHasProductos());
        return "pedido/carrito";
    }

    @GetMapping("/checkout")
    public String Comprar(@ModelAttribute("pedido") Pedido pedido,Model model){


     //   ped.getListPedidoHasProductos();

        return "pedido/checkout";
    }

    @PostMapping("/pagarYguardarCompra")
    public String GuardarCompra(@ModelAttribute("pedido") Pedido pedido,
                                RedirectAttributes attr,Model model){

        String[] arrOfStr = pedido.getCreditCard().split("(?<=[0-9])");
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
            attr.addFlashAttribute("msg","Compra exitosa");
            Optional<Integer> opt = pedidoRepository.hallarAutoincrementalPedido();
            int autoincremental = opt.get();
            LocalDate lt = LocalDate.now();
            String codigo = "PE" + lt.getDayOfMonth() + lt.getMonthValue() + lt.getYear() + (autoincremental+1);
            pedido.setCodigo(codigo);
            pedidoRepository.save(pedido);

            List<PedidoHasProducto> listaPedProd = pedido.getListPedidoHasProductos();
            for (PedidoHasProducto pedidoHasProducto: listaPedProd){
                PedProdId id = pedidoHasProducto.getId();
                Producto prdct = id.getProducto();
                prdct.setStock(prdct.getStock()-pedidoHasProducto.getCant());
            }
            return "redirect:/productos/";
        }else{
            return "redirect:/compras/checkout";
        }
    }




    @GetMapping("/pedidos")
    public String Pedidos(Model model,HttpSession session){

        Usuario u = (Usuario) session.getAttribute("usuario");
        model.addAttribute("pedidos", pedidoRepository.findByUsuario(u.getDni()));
        return "pedido/listaPedidos";
    }
}
