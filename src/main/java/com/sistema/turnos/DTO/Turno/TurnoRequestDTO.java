package com.sistema.turnos.DTO.Turno;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class TurnoRequestDTO {

    @NotNull(message = "El servicio es obligatorio")
    private UUID servicioId;
    @NotNull(message = "La fecha y hora es obligatoria")
    @Future(message = "La fecha debe ser futura")
    private LocalDateTime fechaHora;

    private String comentario;
}
