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
                    ped.setTotal(ped.getTotal() + php.getSubtotal());
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

        float a = 0;
        for(PedidoHasProducto php : pedido.getListPedidoHasProductos() ){
            a = a + php.getSubtotal();
        }
        pedido.setTotal(a);
        model.addAttribute("total",pedido.getTotal());


        for (PedidoHasProducto php : pedido.getListPedidoHasProductos()){
            System.out.println(php.getId().getProducto().getNombre());
        }

        int cantidad = 0;
        cantidad = optPed.map(this::productosTotalesEnCarrito).orElse(0);


        model.addAttribute("carrito", cantidad);
        return "pedido/carrito";
    }


    @GetMapping("/checkout")
    public String Comprar(@ModelAttribute("pedido") Pedido pedido,Model model){
     //   ped.getListPedidoHasProductos();
        model.addAttribute("total", pedido.getTotal());
        return "pedido/checkout";
    }

    @PostMapping("/pagarYguardarCompra")
    public String GuardarCompra(@ModelAttribute("pedido") Pedido pedido, HttpSession session,
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

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        //System.out.println(usuario.getDni());
        Optional<Pedido> optPed = pedidoRepository.findById("carrito_"+ Integer.toString(usuario.getDni()));
        //Pedido pedidoFinal = optPed.get();
        //Pedido ped = optPed.get();

        int verifier = (10 - (sum % 10)) % 10;
        //System.out.println(verifier);
        //System.out.println(sum);
        if (verifier == intArray[15] && optPed.isPresent()){
            Pedido newPedido = new Pedido(optPed.get(),pedidoRepository.hallarAutoincrementalPedido());
            pedidoRepository.save(optPed.get());
            pedidoRepository.save(newPedido);
            /*
            List<PedidoHasProducto> phpList = pedidoFinal.getListPedidoHasProductos();
            attr.addFlashAttribute("msg","Compra exitosa");
            int autoincremental = pedidoRepository.hallarAutoincrementalPedido();
            //System.out.println(autoincremental);
            LocalDate lt = LocalDate.now();
            String codigo = "PE" + lt.getDayOfMonth() + lt.getMonthValue() + lt.getYear() + (autoincremental+1);
            //System.out.println(codigo);
            pedidoFinal.setCodigo(codigo);//
            pedidoFinal.setFecha_compra(lt);//
            pedidoFinal.setComprado(1);//
            pedidoFinal.setUsuario(usuario);//
            for (PedidoHasProducto php : phpList){
                php.setId(new PedProdId(pedidoFinal, php.getId().getProducto()));
                php.setSubtotal(php.getSubtotal());
                php.setCant(php.getCant());
                pedidoHasProductoRepository.save(php);
                pedidoHasProductoRepository.delete(php);
            }
            ped.setUsuario(usuario);
            ped.setCodigo("carrito_"+ Integer.toString(usuario.getDni()));
            ped.setComprado(0);
            ped.setFecha_compra(lt);
            float a = 0;
            ped.setTotal(a);
            pedidoRepository.save(ped);
            pedidoRepository.save(pedidoFinal);
            */

            List<PedidoHasProducto> listaPedProd = newPedido.getListPedidoHasProductos();
            for (PedidoHasProducto pedidoHasProducto: listaPedProd){
                Producto prdct = pedidoHasProducto.getId().getProducto();
                prdct.setStock(prdct.getStock()-pedidoHasProducto.getCant());
            }

            return "redirect:/productos";
        }else{
            attr.addFlashAttribute("msg","Tarjeta incorrecta");
            return "redirect:/u/checkout";
        }
    }




    @GetMapping("/pedidos")
    public String Pedidos(Model model,HttpSession session){

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("pedidos", pedidoRepository.findByUsuarioAndComprado(usuario,0));



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

    @GetMapping("/borrarUnidad")
    public String borrarUnidad(@RequestParam("cod") String cod, HttpSession session){

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<Producto> optProd = productoRepository.findById(cod);
        System.out.println(usuario.getDni());
        Optional<Pedido> optPed = pedidoRepository.findById("carrito_"+ Integer.toString(usuario.getDni()));
        Pedido ped = optPed.get();
        for(PedidoHasProducto php : ped.getListPedidoHasProductos()){
            if(php.getId().getProducto().getCodigo().equals(cod)){
                php.setCant(php.getCant()-1);
                php.setSubtotal(php.getCant()*php.getId().getProducto().getPrecio());
                if(php.getCant()==0){
                    pedidoHasProductoRepository.delete(php);
                }else{
                    pedidoHasProductoRepository.save(php);
                }
            }
        }

        return "redirect:/u/carrito";
    }


    public int productosTotalesEnCarrito(Pedido pedido){
        int sum = 0;
        for(PedidoHasProducto php : pedido.getListPedidoHasProductos()){
            sum = sum + php.getCant();
        }
        return sum;
    }
}
