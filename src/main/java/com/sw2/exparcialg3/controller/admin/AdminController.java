package com.sw2.exparcialg3.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping(value = {"","/"})
    public String asdasda(){
        return "redirect:/admin/gestores";
    }

}
