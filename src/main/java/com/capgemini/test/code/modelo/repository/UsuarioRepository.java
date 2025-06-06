package com.capgemini.test.code.modelo.repository;

import com.capgemini.test.code.modelo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    Optional<Usuario> findByEmail(String email);
}
