package com.sw2.exparcialg3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @GetMapping("/{page}")
    public String dummyGet(@PathVariable String page){
        return page;
    }

    @GetMapping("/producto/{page}")
    public String dummyGetProduct(@PathVariable String page){
        return "producto/"+ page;
    }

    @GetMapping("/pedido/{page}")
<<<<<<< HEAD
    public String dummyGetPed(@PathVariable String page){
=======
    public String dummyGetPedido(@PathVariable String page){
>>>>>>> b8f541544d5e4b40e35e868225600e9b6f8b42a1
        return "pedido/"+ page;
    }

}
