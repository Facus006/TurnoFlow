package com.sistema.turnos.Repositorios;

import com.sistema.turnos.Entidades.Turno;
import com.sistema.turnos.Enum.EstadoTurno;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnoRepositorio extends JpaRepository<Turno, UUID> {

    Page<Turno> findByClienteId(UUID clienteId, Pageable pageable);

    Page<Turno> findByNegocioId(UUID negocioId, Pageable pageable);

    List<Turno> findByNegocioIdAndFechaHoraBetween(
            UUID negocioId,
            LocalDateTime inicio,
            LocalDateTime fin
    );

    List<Turno> findByEstado(EstadoTurno estado);

    boolean existsByServicioIdAndFechaHoraAndEstadoIn(
            UUID servicioId,
            LocalDateTime fechaHora,
            List<EstadoTurno> estados
    );
}
