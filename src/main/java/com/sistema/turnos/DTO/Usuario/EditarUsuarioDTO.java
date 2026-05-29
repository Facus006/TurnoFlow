package com.sistema.turnos.DTO.Usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditarUsuarioDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellido;
}
