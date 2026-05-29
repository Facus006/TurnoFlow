package com.sistema.turnos.DTO.Usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditarPasswordDTO {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String password;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "Debe tener al menos 6 caracteres")
    private String nuevoPassword;

    @NotBlank(message = "Debe repetir la nueva contraseña")
    private String nuevoPassword2;
}
