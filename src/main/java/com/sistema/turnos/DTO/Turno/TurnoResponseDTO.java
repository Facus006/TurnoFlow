package com.sistema.turnos.DTO.Turno;

import com.sistema.turnos.Enum.EstadoTurno;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class TurnoResponseDTO {

    private UUID id;

    private LocalDateTime fechaHora;

    private LocalDateTime fechaCreacion;

    private String comentario;

    private EstadoTurno estado;

    private String negocioNombre;

    private String servicioNombre;

    private String clienteNombre;

}
