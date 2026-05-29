package com.sistema.turnos.Controladores;

import com.sistema.turnos.DTO.Auth.AuthResponse;
import com.sistema.turnos.DTO.Usuario.ActualizarEmailDTO;
import com.sistema.turnos.DTO.Usuario.EditarPasswordDTO;
import com.sistema.turnos.DTO.Usuario.EditarUsuarioDTO;
import com.sistema.turnos.DTO.Usuario.UsuarioResponseDTO;
import com.sistema.turnos.Entidades.Usuario;
import com.sistema.turnos.Servicios.RefreshTokenService;
import com.sistema.turnos.Servicios.UsuarioServicio;
import com.sistema.turnos.jwt.JwtService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioControlador {

    private final JwtService jwtService;
    private final UsuarioServicio usuarioServicio;
    private final RefreshTokenService refreshTokenService;

    @PutMapping("/editar")
    public ResponseEntity<UsuarioResponseDTO> edit(
            @Valid @RequestBody EditarUsuarioDTO dto) {
        return ResponseEntity.ok(usuarioServicio.editarUsuario(dto));
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<UsuarioResponseDTO>> all(Pageable pageable) {
        return ResponseEntity.ok(
                usuarioServicio.listarUsuariosActivos(pageable)
        );
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<UsuarioResponseDTO>> buscarPorNombre(
            @RequestParam String nombre, Pageable pageable) {
        return ResponseEntity.ok(
                usuarioServicio.buscarUsuarioPorNombre(nombre, pageable)
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<UsuarioResponseDTO> getOne(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                usuarioServicio.obtenerPorId(id)
        );
    }

    @PutMapping("/password")
    public ResponseEntity<AuthResponse> editPassword(
            @Valid @RequestBody EditarPasswordDTO dto) {

        Usuario usuarioActualizado = usuarioServicio.editarPassword(dto);

        refreshTokenService.eliminarPorUsuario(usuarioActualizado);

        String accessToken = jwtService.generateToken(usuarioActualizado);
        String refreshToken = refreshTokenService
                .crearRefreshToken(usuarioActualizado.getEmail())
                .getToken();

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PutMapping("/email")
    public ResponseEntity<AuthResponse> editEmail(
            @Valid @RequestBody ActualizarEmailDTO dto) {

        Usuario usuarioActualizado = usuarioServicio.actualizarEmail(dto);

        refreshTokenService.eliminarPorUsuario(usuarioActualizado);

        String accessToken = jwtService.generateToken(usuarioActualizado);
        String refreshToken = refreshTokenService
                .crearRefreshToken(usuarioActualizado.getEmail())
                .getToken();

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

}
