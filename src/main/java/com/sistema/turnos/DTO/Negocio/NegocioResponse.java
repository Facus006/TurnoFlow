package com.sistema.turnos.DTO.Negocio;

import java.util.UUID;
import lombok.Data;

@Data
public class NegocioResponse {

    private UUID id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String direccion;
    private String telefono;
    private UUID propietarioId;
    private String propietarioNombre;
    private boolean activo;

}
