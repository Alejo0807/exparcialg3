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
import java.time.LocalDate;
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
                AtomicBoolean flag2 = new AtomicBoolean(false); // indica si el producto ya está en el carrito

                carritoPedido.getListPedidoHasProductos().forEach((php)->
                {   System.out.println("La cantidad es "+php.getCant());
                    if(producto.getCodigo().equals(php.getId().getProducto().getCodigo()) && (php.getCant() < 4)){
                        flag.set(true);
                        php.setCant(php.getCant()+1);
                        pedidoHasProductoRepository.save(php);
                        attr.addFlashAttribute("msg","Producto agregado al carrito");
                }else{attr.addFlashAttribute("msg2","Se alcanzo el limite de unidades por producto(4)");
                        flag2.set(true);
                } });
                if (!flag.get() && !flag2.get()){
                    pedidoHasProductoRepository.save(new PedidoHasProducto(
                            new PedProdId(carritoPedido, producto), 1
                    ));
                    attr.addFlashAttribute("msg","Producto agregado al carrito");
                }
                pedidoRepository.udpate_carrito(carritoPedido.getCodigo(),carritoPedido.getTotal() + producto.getPrecio());
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


    @PostMapping("/checkout")
    public String Comprar(@RequestParam("t") float total, @ModelAttribute("pedido") Pedido pedido,Model model){
     //   ped.getListPedidoHasProductos();
        model.addAttribute("total", total);
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
        if (verifier == intArray[15] && (carritoPedido!=null)){
            //Generar el pedido
            Integer numPed = pedidoRepository.hallarAutoincrementalPedido();
            String codigoDePedido = carritoPedido.getCodeForPedido((numPed==null?0:numPed) + 1);
            pedidoRepository.new_pedido(codigoDePedido,
                    usuario.getDni(), carritoPedido.getTotal());
            pedidoHasProductoRepository.saveAll(new ArrayList<PedidoHasProducto>(){
                {
                    carritoPedido.getListPedidoHasProductos().forEach((i)->{
                        add(new PedidoHasProducto(new PedProdId(new Pedido(codigoDePedido), i.getId().getProducto()), i.getCant()));
                    });
                }
            });
            //Borrar el pedido del carrito
            pedidoRepository.udpate_carrito(carritoPedido.getCodigo(), 0);
            pedidoHasProductoRepository.deleteInBatch(carritoPedido.getListPedidoHasProductos());
            System.out.println(3);

            List<PedidoHasProducto> listaPedProd = carritoPedido.getListPedidoHasProductos();
            for (PedidoHasProducto pedidoHasProducto: listaPedProd){
                Producto prdct = pedidoHasProducto.getId().getProducto();
                prdct.setStock(prdct.getStock()-pedidoHasProducto.getCant());
            }
            attr.addFlashAttribute("msg","Compra exitosa");
            return "redirect:/productos";
        }else{
            attr.addFlashAttribute("msg","Tarjeta incorrecta");
            return "redirect:/u/checkout";
        }
    }




    @GetMapping("/pedidos")
    public String Pedidos(Model model,HttpSession session){

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        List<Pedido> list = pedidoRepository.findByUsuarioAndComprado(usuario,1);

        list.forEach(Pedido::getListPedidoHasProductos);

        model.addAttribute("pedidos", list );
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
