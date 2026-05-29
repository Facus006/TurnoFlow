package com.sistema.turnos.DTO.Servicio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ServicioRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;
    @NotBlank(message = "La descripcion no puede estar vacía")
    private String descripcion;
    @NotNull(message = "El precio no puede estar vacío")
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    @NotNull(message = "La duracion no puede estar vacía")
    @Positive(message = "La duracion debe ser mayo a 0")
    private Integer duracionMinutos;

}
