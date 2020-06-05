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
    public String dummyGetP(@PathVariable String page){
        return "producto/"+ page;
    }

}
