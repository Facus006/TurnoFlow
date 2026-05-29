package com.sistema.turnos.Controladores;

import com.sistema.turnos.DTO.Auth.AuthRequest;
import com.sistema.turnos.DTO.Auth.AuthResponse;
import com.sistema.turnos.DTO.RefreshTokenRequest;
import com.sistema.turnos.DTO.Usuario.UsuarioRequestDTO;
import com.sistema.turnos.Entidades.RefreshToken;
import com.sistema.turnos.Entidades.Usuario;
import com.sistema.turnos.Servicios.RefreshTokenService;
import com.sistema.turnos.Servicios.UsuarioServicio;
import com.sistema.turnos.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControlador {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioServicio usuarioServicio;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioServicio.buscarUsuarioPorEmail(request.getEmail());

        String accessToken = jwtService.generateToken(usuario);
        String refreshToken = refreshTokenService.crearRefreshToken(usuario.getEmail()).getToken();

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));

    }

    @PostMapping("/registro")
    public ResponseEntity<Void> registro(@RequestBody @Valid UsuarioRequestDTO dto) {
        usuarioServicio.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenService.verificarToken(request.getRefreshToken());

        Usuario usuario = refreshToken.getUsuario();

        String accessToken = jwtService.generateToken(usuario);

        return ResponseEntity.ok(
                new AuthResponse(accessToken, request.getRefreshToken())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenService.verificarToken(request.getRefreshToken());

        refreshTokenService.eliminarPorUsuario(refreshToken.getUsuario());

        return ResponseEntity.ok("Logout exitoso");
    }
}
