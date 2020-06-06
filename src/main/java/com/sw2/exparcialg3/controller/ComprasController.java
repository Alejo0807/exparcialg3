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
    @Autowired
    ProductoRepository productoRepository;

    @GetMapping("/agregarAlCarrito")
    public String AgregarCarrito(@RequestParam(name = "id") String cod, HttpSession session, RedirectAttributes attr){

        //System.out.println("hola");
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<Producto> optProd = productoRepository.findById(cod);
        System.out.println(usuario.getDni());
        Optional<Pedido> optPed = pedidoRepository.findById("carrito_"+ Integer.toString(usuario.getDni()));

        Pedido pedido = optPed.get();
        System.out.println(pedido.getUsuario().getDni());

        if(optProd.isPresent()){
            Producto prod = optProd.get();
            System.out.println("Stock de producto:"+ prod.getStock());
            PedidoHasProducto phpfinal = null;
            if(optPed.isPresent()){
                Pedido ped = optPed.get();
                     System.out.println("Codigo de pedido:"+ ped.getCodigo());
                List<PedidoHasProducto> listaPHP = ped.getListPedidoHasProductos();

                String phpActualizado = "";

                for(PedidoHasProducto php : listaPHP ){
                    if(prod.getCodigo() == php.getId().getProducto().getCodigo()){
                        phpActualizado = "orden actualizada";
                        phpfinal = php;
                    }
                }
                if (phpActualizado != "orden actualizada" ){
                    PedidoHasProducto php = new PedidoHasProducto(new PedProdId(ped, prod), 1);
                    php = crearPHP(php);
                    phpfinal = php;
                }
                phpfinal = llenarPHP(phpfinal);
                pedidoRepository.save(ped);

                pedidoHasProductoRepository.save(phpfinal);
                attr.addFlashAttribute("msg","Producto agregado al carrito");
                //guardar de otra manera
            }else{
                Pedido ped = new Pedido();
                ped.setCodigo("carrito_"+ Integer.toString(usuario.getDni()));
                PedidoHasProducto php = new PedidoHasProducto();
                php.setId(new PedProdId(ped, prod));
                phpfinal = crearPHP(php);
                attr.addFlashAttribute("msg","Nuevo carrito creado y producto agregado");
                pedidoRepository.save(ped);

                pedidoHasProductoRepository.save(phpfinal);
            }

            return "redirect:/productos";
        }else{
            attr.addFlashAttribute("msg","Stock agotado");
            return "redirect:/productos";
        }

    }

    public PedidoHasProducto llenarPHP(PedidoHasProducto php){
        php.setCant(php.getCant() + 1);
        php.setSubtotal(php.getSubtotal() + php.getId().getProducto().getPrecio());
        return php;
    }

    public PedidoHasProducto crearPHP(PedidoHasProducto php){
        float a = 0;
        php.setSubtotal(a);
        php.setCant(0);
        return  php;
    }





    @GetMapping("/carrito")
    public String Carrito(Model model, HttpSession session){

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        System.out.println(usuario.getDni());
        Optional<Pedido> optPed = pedidoRepository.findById("carrito_"+ Integer.toString(usuario.getDni()));
        Pedido pedido = optPed.get();
        model.addAttribute("listaPedido", pedido.getListPedidoHasProductos());

        for (PedidoHasProducto php : pedido.getListPedidoHasProductos()){
            System.out.println(php.getId().getProducto().getNombre());
        }

        int cantidad = 0;
        if (optPed.isPresent()){
            cantidad = productosTotalesEnCarrito(optPed.get());
        }else{
            cantidad = 0;
        }


        model.addAttribute("carrito", cantidad);
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
            attr.addFlashAttribute("msg","Tarjeta incorrecta");
            return "redirect:/compras/checkout";
        }
    }




    @GetMapping("/pedidos")
    public String Pedidos(Model model,HttpSession session){

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("pedidos", pedidoRepository.findByUsuario(usuario));



        int cantidad = 0;
        Optional<Pedido> optPed = pedidoRepository.findById("carrito_"+ Integer.toString(usuario.getDni()));
        if (optPed.isPresent()){
            cantidad = productosTotalesEnCarrito(optPed.get());
        }else{
            cantidad = 0;
        }

        model.addAttribute("carrito", cantidad);

        return "pedido/listaPedidos";
    }

/*    @PostMapping("/borrarUnidad")
    public String borrarUnidad(@RequestParam("cod") String cod, HttpSession session){

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<Producto> optProd = productoRepository.findById(cod);
        System.out.println(usuario.getDni());
        Optional<Pedido> optPed = pedidoRepository.findById("carrito_"+ Integer.toString(usuario.getDni()));

    }*/


    public int productosTotalesEnCarrito(Pedido pedido){
        int sum = 0;
        for(PedidoHasProducto php : pedido.getListPedidoHasProductos()){
            sum = sum + php.getCant();
        }
        return sum;
    }
}
