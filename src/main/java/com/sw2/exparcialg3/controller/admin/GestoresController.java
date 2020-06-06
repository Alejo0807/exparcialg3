package com.sw2.exparcialg3.controller.admin;

import com.sw2.exparcialg3.entity.Rol;
import com.sw2.exparcialg3.entity.Usuario;
import com.sw2.exparcialg3.repository.UsuarioRepository;
import com.sw2.exparcialg3.utils.CustomMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/admin/gestores")
public class GestoresController {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    CustomMailService customMailService;

    private final int ROL_CRUD = 2; // rol al que se le hace crud;

    @GetMapping(value = {""})
    public String lista(Model model){

        model.addAttribute("lista", usuarioRepository.findUsuariosByRol_Idrol(ROL_CRUD));
        return "admin/listaGestores";
    }

    @GetMapping("/edit")
    public  String asdnadsin(@RequestParam("id") int dni, @ModelAttribute("usuario") Usuario usuario, Model model){

        Optional<Usuario> optionalUsuario = usuarioRepository.findUsuarioByDniAndRol_Idrol(dni, ROL_CRUD);
        if(optionalUsuario.isPresent()){
            usuario = optionalUsuario.get();
            model.addAttribute("formtype", "0");
            model.addAttribute("usuario", usuario);

            return "admin/formGestores";
        }
        return "redirect:/admin/gestores";
    }

    @GetMapping("/new")
    public  String asdnadsin(@ModelAttribute("usuario") Usuario usuario, Model model){
        model.addAttribute("formtype", "1");
        return "admin/formGestores";
    }


    @PostMapping("/save")
    public  String asdnaasdasddsin(@ModelAttribute("usuario") @Valid Usuario usuario,
                                   BindingResult bindingResult, @RequestParam("type") int type,
                                   Model model, RedirectAttributes attr) throws IOException, MessagingException {

        if(type==1 && usuarioRepository.findById(usuario.getDni()).isPresent()){ //if new
            bindingResult.rejectValue("dni","error.user","Este dni ya existe");
        }

        String s = usuario.getCorreo().replace(" ","");
        if(!(s.endsWith("@pucp.edu.pe") || s.endsWith("@pucp.pe"))){
            bindingResult.rejectValue("correo","error.user","El correo debe ser pucp.edu.pe o pucp.pe");
        }

        if(bindingResult.hasErrors()){
            model.addAttribute("formtype",Integer.toString(type));
            return "admin/formGestores";
        }
        else {

            Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuario.getDni());
            if (optionalUsuario.isPresent()) {
                usuarioRepository.update_usuario(usuario.getDni(),
                        usuario.getNombre(), usuario.getApellido(), usuario.getCorreo());
                attr.addFlashAttribute("msgSuccess", "Gestor actualizado exitosamente");
            }
            else if (type==1){ // if new user
                customMailService.sendEmail(usuario.getCorreo(),
                        "Registro de gestor de bodega", "Bienvenido Gestor",
                        "Este es un mensaje de validación de su cuenta, para ingresar al sistema use su correo y la siguiente contraseña\n"
                                +usuario.generateNewPassword());
                usuario.setRol(new Rol(ROL_CRUD));
                usuario.setEnable(true);
                usuarioRepository.save(usuario);
                attr.addFlashAttribute("msgSuccess", "Gestor creado exitosamente");
            }
            return "redirect:/admin/gestores";
        }
    }


    @GetMapping("/delete")
    public  String asdnadsin(@RequestParam("id") int dni, RedirectAttributes attr){

        Optional<Usuario> optionalUsuario = usuarioRepository.findUsuarioByDniAndRol_Idrol(dni, ROL_CRUD);
        if(optionalUsuario.isPresent()){
            Usuario usuario = optionalUsuario.get();
            try {
                usuarioRepository.delete(usuario);
                attr.addFlashAttribute("msgSuccess","Gestor borrado exitosamente");
            }
            catch (Exception e){
                attr.addFlashAttribute("msgError", "Este gestor no se puede borrar");
            }
        }
        return "redirect:/admin/gestores";
    }


}
