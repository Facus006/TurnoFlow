package com.sistema.turnos.DTO.Servicio;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class ServicioResponse {

    private UUID id;

    private String nombre;

    private String descripcion;

    private BigDecimal precio;

    private Integer duracionMinutos;

    private boolean activo;

    private UUID negocioId;

    private String negocioNombre;

}
