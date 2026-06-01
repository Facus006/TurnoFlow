package com.sistema.turnos.Controladores;

import com.sistema.turnos.DTO.Turno.TurnoResponseDTO;
import com.sistema.turnos.DTO.Usuario.UsuarioResponseDTO;
import com.sistema.turnos.Servicios.TurnoServicio;
import com.sistema.turnos.Servicios.UsuarioServicio;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminControlador {

    private final UsuarioServicio usuarioServicio;
    private final TurnoServicio turnoServicio;

    @GetMapping("/usuario/all")
    public ResponseEntity<Page<UsuarioResponseDTO>> all(Pageable pageable) {
        return ResponseEntity.ok(usuarioServicio.listarUsuarios(pageable));
    }

    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable("id") UUID id) {

        usuarioServicio.eliminarUsuarioPorId(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/usuario/{id}/rol")
    public ResponseEntity<Void> cambiarRol(
            @PathVariable("id") UUID id) {

        usuarioServicio.alternarRolAdmin(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/usuario/{id}/estado")
    public ResponseEntity<Void> toggleEstadoUsuario(@PathVariable("id") UUID id) {

        usuarioServicio.cambiarEstadoUsuario(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/usuario/{id}/rol-negocio")
    public ResponseEntity<Void> setRolNegocio(
            @PathVariable("id") UUID id) {
        usuarioServicio.setNegocio(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/negocio/{id}/rol-user")
    public ResponseEntity<Void> setRolUser(
            @PathVariable("id") UUID id) {
        usuarioServicio.quitarRolNegocio(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/negocio/{id}")
    public ResponseEntity<Page<TurnoResponseDTO>> negocio(
            @PathVariable("id") UUID id, Pageable pageable) {
        return ResponseEntity.ok(turnoServicio.turnosNegocio(id, pageable));
    }

}
