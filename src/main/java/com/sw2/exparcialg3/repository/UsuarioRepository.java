package com.sw2.exparcialg3.repository;

import com.sw2.exparcialg3.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByCorreo(String correo);

    List<Usuario> findUsuariosByRol_Idrol(int idrol);

    Optional<Usuario> findUsuarioByDniAndRol_Idrol(int dni, int idrol);

    @Procedure(name = "update_usuario")
    void update_usuario(int dni, String nombre, String apellido, String correo);

    @Procedure(name = "save_usuario")
    void save_usuario(int dni, String nombre, String apellido, String correo, String password);

}
