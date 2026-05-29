package com.sistema.turnos.Repositorios;

import com.sistema.turnos.Entidades.Servicio;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepositorio extends JpaRepository<Servicio, UUID> {

    Page<Servicio> findByNegocioId(UUID negocioId, Pageable pageable);

    Page<Servicio> findByNegocioIdAndActivoTrue(UUID negocioId, Pageable pageable);
}
