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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
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
    public String Lista(Model model, HttpSession session){

        model.addAttribute("productos", productoRepository.findByStockIsGreaterThan(0));

        int cantidad = 0;
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if(usuario != null){
            Optional<Pedido> optPed = pedidoRepository.findById("carrito_"+ Integer.toString(usuario.getDni()));
            if (optPed.isPresent()){
                cantidad = productosTotalesEnCarrito(optPed.get());
            }else{
                cantidad = 0;
            }
        }

        model.addAttribute("carrito", cantidad);

        return "producto/listaProducto";
    }

    public int productosTotalesEnCarrito(Pedido pedido){
        int sum = 0;
        for(PedidoHasProducto php : pedido.getListPedidoHasProductos()){
            sum = sum + php.getCant();
        }
        return sum;
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

    @PostMapping("/buscar")
    public String buscarProd(@RequestParam("search") String param, Model model){

        model.addAttribute("productos", productoRepository.buscarProductos(param));
        model.addAttribute("search",param);
        return "producto/listaProducto";
    }


}
