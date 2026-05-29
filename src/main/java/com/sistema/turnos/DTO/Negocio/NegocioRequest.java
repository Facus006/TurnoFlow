package com.sistema.turnos.DTO.Negocio;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NegocioRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;
    @NotBlank(message = "La descripcion no puede estar vacía")
    private String descripcion;
    @NotBlank(message = "La categoria no puede estar vacío")
    private String categoria;
    @NotBlank(message = "La direccion no puede estar vacío")
    private String direccion;
    @NotBlank(message = "El telefono no puede estar vacío")
    private String telefono;

}
