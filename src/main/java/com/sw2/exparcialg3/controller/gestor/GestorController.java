package com.sw2.exparcialg3.controller.gestor;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gestor")
public class GestorController {
    @GetMapping("")
    public String adasdafinficm(){
        return "index";
    }

}
