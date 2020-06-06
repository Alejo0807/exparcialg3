package com.sw2.exparcialg3.repository;

import com.sw2.exparcialg3.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    Rol findById(int rol);
}
