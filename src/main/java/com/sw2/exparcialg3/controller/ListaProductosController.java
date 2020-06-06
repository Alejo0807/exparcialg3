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
import java.util.ArrayList;
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

    private final int PAGES = 7;

    @GetMapping(value = {"", "/"})
    public String Lista(Model model, HttpSession session, @RequestParam(value = "search", required = false) String param,
                        @RequestParam(value = "page", required = false) Integer page){

        if (page!=null){
            param=(String)session.getAttribute("lastSearch");
        }else {
            page=1;
        }
        model.addAttribute("search",param);
        if (param==null) {
            param="";
        }

        List<Producto> list = productoRepository.buscarProductos(param, (page-1)*PAGES, PAGES );
        ArrayList<Integer> pages = new ArrayList<>();
        int productsSize = productoRepository.contar(param).getCantidad();
        for (int jj = 0 ; jj < (productsSize)/7+1; jj++){
            pages.add(jj+1);
        }
        model.addAttribute("pages",pages);
        model.addAttribute("productos", list);
        session.setAttribute("lastSearch", param);


        int cantidad = 0;
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if(usuario != null){
            Optional<Pedido> optPed = pedidoRepository.findById("carrito_"+ Integer.toString(usuario.getDni()));
            cantidad = optPed.map(this::productosTotalesEnCarrito).orElse(0);
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



}
