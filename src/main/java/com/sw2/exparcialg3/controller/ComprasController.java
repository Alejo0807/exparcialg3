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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

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
        Producto producto = productoRepository.findById(cod).orElse(null);
        Pedido carritoPedido = pedidoRepository.findById("carrito_"+usuario.getDni()).orElse(null);

        if(producto!=null && producto.getStock()>0){
            if(carritoPedido!=null){
                AtomicBoolean flag = new AtomicBoolean(false); // indica si el producto ya está en el carrito
                carritoPedido.getListPedidoHasProductos().forEach((php)->
                { if(producto.getCodigo().equals(php.getId().getProducto().getCodigo())){
                        flag.set(true);
                        php.setCant(php.getCant()+1);
                        pedidoHasProductoRepository.save(php);
                    } });
                if (!flag.get()){
                    pedidoHasProductoRepository.save(new PedidoHasProducto(
                            new PedProdId(carritoPedido, producto), 1
                    ));
                }
                pedidoRepository.udpate_carrito(carritoPedido.getCodigo(),carritoPedido.getTotal() + producto.getPrecio());
                attr.addFlashAttribute("msg","Producto agregado al carrito");
            }else{
                attr.addFlashAttribute("msg","Ocurrio un problema");
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
        Pedido carritoPedido = pedidoRepository.findById("carrito_"+ usuario.getDni()).orElse(null);

        int verifier = (10 - (sum % 10)) % 10;
        //System.out.println(verifier);
        //System.out.println(sum);
        if (verifier == intArray[15] && (carritoPedido!=null)){
            Pedido newPedido = new Pedido(carritoPedido,pedidoRepository.hallarAutoincrementalPedido());
            System.out.println(1);
            pedidoRepository.udpate_carrito(carritoPedido.getCodigo(), 0);
            System.out.println(2);
            pedidoHasProductoRepository.deleteInBatch(carritoPedido.getListPedidoHasProductos());
            System.out.println(3);
            pedidoRepository.new_pedido(newPedido.getCodigo(), usuario.getDni(), newPedido.getTotal());
            System.out.println(4);
            pedidoHasProductoRepository.saveAll(carritoPedido.getListPedidoHasProductos());
            System.out.println(5);

            List<PedidoHasProducto> listaPedProd = newPedido.getListPedidoHasProductos();
            for (PedidoHasProducto pedidoHasProducto: listaPedProd){
                Producto prdct = pedidoHasProducto.getId().getProducto();
                System.out.println(6);
                prdct.setStock(prdct.getStock()-pedidoHasProducto.getCant());
                System.out.println(7);
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
