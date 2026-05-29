package com.sistema.turnos.Repositorios;

import com.sistema.turnos.Entidades.Usuario;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, UUID> {

    Usuario findByEmail(String email);

    Page<Usuario> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<Usuario> findByEnabledTrue(Pageable pageable);
}
