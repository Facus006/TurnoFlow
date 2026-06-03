package com.sistema.turnos;

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
import com.sistema.turnos.Servicios.NegocioServicio;
import com.sistema.turnos.Servicios.TurnoServicio;
import com.sistema.turnos.Servicios.UsuarioServicio;
import com.sistema.turnos.errores.MyException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class TurnoServicioTest {

    @Mock
    private UsuarioServicio usuarioServicio;

    @Mock
    private ServicioRepositorio servicioRepositorio;

    @Mock
    private TurnoRepositorio turnoRepositorio;

    @Mock
    private NegocioServicio negocioServicio;

    @InjectMocks
    private TurnoServicio turnoServicio;

    @Test
    void reservarTurno_deberiaLanzarExcepcion_siHorarioOcupado() {

        // ARRANGE
        UUID servicioId = UUID.randomUUID();
        LocalDateTime fechaHora = LocalDateTime.of(2026, 6, 10, 10, 0);

        TurnoRequestDTO dto = new TurnoRequestDTO();
        dto.setServicioId(servicioId);
        dto.setFechaHora(fechaHora);
        dto.setComentario("Test");

        Usuario usuarioLogueado = new Usuario();
        usuarioLogueado.setId(UUID.randomUUID());

        Servicio servicio = new Servicio();
        servicio.setId(servicioId);
        servicio.setNegocio(new Negocio());

        when(usuarioServicio.obtenerUsuarioLogueado())
                .thenReturn(usuarioLogueado);

        when(servicioRepositorio.findById(servicioId))
                .thenReturn(Optional.of(servicio));

        when(turnoRepositorio.existsByServicioIdAndFechaHoraAndEstadoIn(
                eq(servicioId),
                eq(fechaHora),
                anyList()
        )).thenReturn(true); // el horario ya está ocupado

        // ACT + ASSERT
        MyException ex = assertThrows(MyException.class, () -> {
            turnoServicio.reservarTurno(dto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        verify(turnoRepositorio, never()).save(any());
    }

    @Test
    void reservarTurno_deberiaGuardarTurno_siHorarioDisponible() {

        // ARRANGE
        UUID servicioId = UUID.randomUUID();
        LocalDateTime fechaHora = LocalDateTime.of(2026, 6, 10, 10, 0);

        TurnoRequestDTO dto = new TurnoRequestDTO();
        dto.setServicioId(servicioId);
        dto.setFechaHora(fechaHora);
        dto.setComentario("Test");

        Usuario usuarioLogueado = new Usuario();
        usuarioLogueado.setId(UUID.randomUUID());
        usuarioLogueado.setNombre("Juan");

        Negocio negocio = new Negocio();
        negocio.setId(UUID.randomUUID());
        negocio.setNombre("Peluquería");

        Servicio servicio = new Servicio();
        servicio.setId(servicioId);
        servicio.setNombre("Corte");
        servicio.setNegocio(negocio);

        when(usuarioServicio.obtenerUsuarioLogueado()).thenReturn(usuarioLogueado);
        when(servicioRepositorio.findById(servicioId)).thenReturn(Optional.of(servicio));
        when(turnoRepositorio.existsByServicioIdAndFechaHoraAndEstadoIn(
                eq(servicioId), eq(fechaHora), anyList()
        )).thenReturn(false); // horario libre

        Turno turnoGuardado = new Turno();
        turnoGuardado.setId(UUID.randomUUID());
        turnoGuardado.setCliente(usuarioLogueado);
        turnoGuardado.setServicio(servicio);
        turnoGuardado.setNegocio(negocio);
        turnoGuardado.setFechaHora(fechaHora);
        turnoGuardado.setEstado(EstadoTurno.PENDIENTE);
        turnoGuardado.setFechaCreacion(LocalDateTime.now());

        when(turnoRepositorio.save(any(Turno.class))).thenReturn(turnoGuardado);

        // ACT
        TurnoResponseDTO resultado = turnoServicio.reservarTurno(dto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(EstadoTurno.PENDIENTE, resultado.getEstado());
        verify(turnoRepositorio, times(1)).save(any(Turno.class));
    }

    @Test
    void cancelarTurno_deberiaLanzarExcepcion_siTurnoYaCompletado() {

        // ARRANGE
        UUID turnoId = UUID.randomUUID();

        Usuario propietario = new Usuario();
        propietario.setId(UUID.randomUUID());

        Negocio negocio = new Negocio();
        negocio.setId(UUID.randomUUID());
        negocio.setPropietario(propietario);

        Usuario cliente = new Usuario();
        cliente.setId(UUID.randomUUID());

        Turno turno = new Turno();
        turno.setId(turnoId);
        turno.setEstado(EstadoTurno.COMPLETADO); // ya completado
        turno.setCliente(cliente);
        turno.setNegocio(negocio);

        Usuario logeado = new Usuario();
        logeado.setId(cliente.getId()); // es el mismo cliente
        logeado.setRole(Role.USER);

        when(usuarioServicio.obtenerUsuarioLogueado()).thenReturn(logeado);
        when(turnoRepositorio.findById(turnoId)).thenReturn(Optional.of(turno));

        // ACT + ASSERT
        MyException ex = assertThrows(MyException.class, () -> {
            turnoServicio.cancelarTurno(turnoId);
        });

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        verify(turnoRepositorio, never()).save(any());
    }

}
