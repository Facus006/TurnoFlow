package com.sistema.turnos.DTO.Negocio;

import java.util.UUID;
import lombok.Data;

@Data
public class NegocioResponse {

    private String nombre;
    private String descripcion;
    private String categoria;
    private String direccion;
    private String telefono;
    private UUID propietarioId;
    private String propietarioNombre;
    
}
