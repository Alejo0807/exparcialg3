package com.sw2.exparcialg3.controller;

import com.sw2.exparcialg3.entity.Producto;
import com.sw2.exparcialg3.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    ProductoRepository productoRepository;

    @GetMapping("/{page}")
    public String dummyGet(@PathVariable String page){
        return page;
    }

    @GetMapping("/producto/{page}")
    public String dummyGetProduct(@PathVariable String page){
        return "producto/"+ page;
    }

    @GetMapping("/pedido/{page}")
    public String dummyGetPedido(@PathVariable String page){
        return "pedido/"+ page;
    }


    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> showImage(@PathVariable("id") String id){
        Optional<Producto> optionalProducto = productoRepository.findById(id);
        if (optionalProducto.isPresent()){
            Producto producto= optionalProducto.get();

            byte[] foto= producto.getFoto();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(producto.getFotocontenttype()));
            return  new ResponseEntity<>(foto, httpHeaders, HttpStatus.OK);
        }
        else {
            return null;
        }


    }


}
