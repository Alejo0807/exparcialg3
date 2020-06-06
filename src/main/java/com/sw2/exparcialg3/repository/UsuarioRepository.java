package com.sw2.exparcialg3.repository;

import com.sw2.exparcialg3.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByCorreo(String correo);

    List<Usuario> findUsuariosByRol_Idrol(int idrol);

    Optional<Usuario> findUsuarioByDniAndRol_Idrol(int dni, int idrol);

}
