package com.sistema.turnos.DTO.Usuario;

import com.sistema.turnos.Enum.Role;
import java.util.UUID;
import lombok.Data;

@Data
public class UsuarioResponseDTO {

    private UUID id;
    private String email;
    private String nombre;
    private String apellido;
    private Role rol;
    private Boolean enabled;

}
