package com.sistema.turnos.Servicios;

import com.sistema.turnos.DTO.Turno.TurnoRequestDTO;
import com.sistema.turnos.DTO.Turno.TurnoResponseDTO;
import com.sistema.turnos.Entidades.Negocio;
import com.sistema.turnos.Entidades.Servicio;
import com.sistema.turnos.Entidades.Turno;
import com.sistema.turnos.Entidades.Usuario;
import com.sistema.turnos.Enum.EstadoTurno;
import com.sistema.turnos.Enum.Role;
import com.sistema.turnos.Repositorios.ServicioRepositorio;
import com.sistema.turnos.Repositorios.TurnoRepositorio;
import com.sistema.turnos.errores.MyException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TurnoServicio {

    private final UsuarioServicio usuarioServicio;
    private final ServicioRepositorio servicioRepositorio;
    private final TurnoRepositorio turnoRepositorio;
    private final NegocioServicio negocioServicio;

    @Transactional
    public TurnoResponseDTO reservarTurno(TurnoRequestDTO dto) {

        Usuario logeado = usuarioServicio.obtenerUsuarioLogueado();

        Servicio servicio = servicioRepositorio
                .findById(dto.getServicioId())
                .orElseThrow(()
                        -> new MyException(
                        "Servicio no encontrado",
                        HttpStatus.NOT_FOUND
                ));

        Negocio negocio = servicio.getNegocio();

        boolean existe = turnoRepositorio
                .existsByServicioIdAndFechaHoraAndEstadoIn(
                        servicio.getId(),
                        dto.getFechaHora(),
                        List.of(
                                EstadoTurno.PENDIENTE,
                                EstadoTurno.CONFIRMADO
                        )
                );

        if (existe) {
            throw new MyException(
                    "Ya existe un turno reservado para esa fecha y hora.",
                    HttpStatus.BAD_REQUEST
            );
        }

        Turno turno = new Turno();

        turno.setCliente(logeado);
        turno.setComentario(dto.getComentario());
        turno.setEstado(EstadoTurno.PENDIENTE);
        turno.setFechaCreacion(LocalDateTime.now());
        turno.setFechaHora(dto.getFechaHora());
        turno.setNegocio(negocio);
        turno.setServicio(servicio);

        turnoRepositorio.save(turno);

        return toDto(turno);
    }

    @Transactional
    public void cancelarTurno(UUID idTurno) {

        Usuario logeado = usuarioServicio.obtenerUsuarioLogueado();

        Turno turno = findById(idTurno);

        if (!turno.getCliente().getId().equals(logeado.getId())
                && !turno.getNegocio()
                        .getPropietario()
                        .getId()
                        .equals(logeado.getId())
                && logeado.getRole() != Role.ADMIN) {

            throw new MyException(
                    "No tienes permisos para cancelar este turno",
                    HttpStatus.FORBIDDEN
            );
        }

        if (turno.getEstado() == EstadoTurno.COMPLETADO) {
            throw new MyException(
                    "No puedes cancelar un turno completado",
                    HttpStatus.FORBIDDEN
            );
        }

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new MyException(
                    "Este turno ya se encuentra cancelado",
                    HttpStatus.FORBIDDEN
            );
        }

        turno.setEstado(EstadoTurno.CANCELADO);
        turnoRepositorio.save(turno);
    }

    @Transactional
    public void confirmarTurno(UUID idTurno) {
        Usuario logeado = usuarioServicio.obtenerUsuarioLogueado();
        Turno turno = findById(idTurno);

        if (!turno.getNegocio().getPropietario().getId()
                .equals(logeado.getId())) {

            throw new MyException(
                    "No tienes permisos para confirmar este turno",
                    HttpStatus.FORBIDDEN
            );
        }

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new MyException(
                    "No puedes confirmar un turno cancelado",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (turno.getEstado() == EstadoTurno.CONFIRMADO) {
            throw new MyException(
                    "Este turno ya está confirmado",
                    HttpStatus.BAD_REQUEST
            );
        }

        turno.setEstado(EstadoTurno.CONFIRMADO);
        turnoRepositorio.save(turno);
    }

    @Transactional
    public void rechazarTurno(UUID idTurno) {

        Usuario logeado = usuarioServicio.obtenerUsuarioLogueado();

        Turno turno = findById(idTurno);

        if (!turno.getNegocio()
                .getPropietario()
                .getId()
                .equals(logeado.getId())
                && logeado.getRole() != Role.ADMIN) {

            throw new MyException(
                    "No tienes permisos para rechazar este turno",
                    HttpStatus.FORBIDDEN
            );
        }

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new MyException(
                    "No puedes rechazar un turno cancelado",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (turno.getEstado() == EstadoTurno.RECHAZADO) {
            throw new MyException(
                    "Este turno ya fue rechazado",
                    HttpStatus.BAD_REQUEST
            );
        }

        turno.setEstado(EstadoTurno.RECHAZADO);
        turnoRepositorio.save(turno);
    }

    @Transactional
    public void completarTurno(UUID idTurno) {

        Usuario logeado = usuarioServicio.obtenerUsuarioLogueado();

        Turno turno = findById(idTurno);

        if (!turno.getNegocio()
                .getPropietario()
                .getId()
                .equals(logeado.getId())
                && logeado.getRole() != Role.ADMIN) {

            throw new MyException(
                    "No tienes permisos para finalizar este turno",
                    HttpStatus.FORBIDDEN
            );
        }

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new MyException(
                    "No puedes completar un turno cancelado",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (turno.getEstado() == EstadoTurno.RECHAZADO) {
            throw new MyException(
                    "No puedes completar un turno rechazado",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (turno.getEstado() == EstadoTurno.COMPLETADO) {
            throw new MyException(
                    "Este turno ya fue completado",
                    HttpStatus.BAD_REQUEST
            );
        }

        turno.setEstado(EstadoTurno.COMPLETADO);
        turnoRepositorio.save(turno);
    }

    @Transactional(readOnly = true)
    public Page<TurnoResponseDTO> misTurnos(Pageable pageable) {
        return turnoRepositorio.findByClienteId(
                usuarioServicio.obtenerUsuarioLogueado().getId(),
                pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<TurnoResponseDTO> turnosMiNegocio(Pageable pageable) {
        Negocio negocio = negocioServicio.obtenerMiNegocio();
        return turnoRepositorio.findByNegocioId(
                negocio.getId(), pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<TurnoResponseDTO> turnosNegocio(UUID id, Pageable pageable) {
        return turnoRepositorio.findByNegocioId(
                id, pageable).map(this::toDto);
    }

    private Turno findById(UUID id) {
        Turno turno = turnoRepositorio.findById(id)
                .orElseThrow(() -> new MyException(
                "Turno no encontrado",
                HttpStatus.NOT_FOUND
        ));
        return turno;
    }

    private TurnoResponseDTO toDto(Turno turno) {
        TurnoResponseDTO dto = new TurnoResponseDTO();
        dto.setClienteNombre(turno.getCliente().getNombre());
        dto.setComentario(turno.getComentario());
        dto.setEstado(turno.getEstado());
        dto.setFechaCreacion(turno.getFechaCreacion());
        dto.setFechaHora(turno.getFechaHora());
        dto.setId(turno.getId());
        dto.setNegocioNombre(turno.getNegocio().getNombre());
        dto.setServicioNombre(turno.getServicio().getNombre());

        return dto;
    }
}
