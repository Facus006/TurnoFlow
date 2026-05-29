package com.sistema.turnos.Repositorios;

import com.sistema.turnos.Entidades.Negocio;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NegocioRepositorio extends JpaRepository<Negocio, UUID> {

    Page<Negocio> findByActivoTrue(Pageable pageable);

    Page<Negocio> findByCategoria(String categoria, Pageable pageable);

    Page<Negocio> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    boolean existsByPropietarioId(UUID propietarioId);

    Optional<Negocio> findByPropietarioId(UUID id);

}
