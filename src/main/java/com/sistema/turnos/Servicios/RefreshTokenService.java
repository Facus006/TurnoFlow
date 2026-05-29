package com.sistema.turnos.Servicios;

import com.sistema.turnos.Entidades.RefreshToken;
import com.sistema.turnos.Entidades.Usuario;
import com.sistema.turnos.Repositorios.RefreshTokenRepositorio;
import com.sistema.turnos.Repositorios.UsuarioRepositorio;
import com.sistema.turnos.errores.MyException;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepositorio refreshTokenRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    private final long REFRESH_TOKEN_DURATION = 1000 * 60 * 60 * 24 * 7; // 7 días

    @Transactional
    public RefreshToken crearRefreshToken(String email) {

        Usuario usuario = usuarioRepositorio.findByEmail(email);

        refreshTokenRepositorio.deleteByUsuario(usuario);
        refreshTokenRepositorio.flush();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION));

        return refreshTokenRepositorio.save(refreshToken);
    }

    @Transactional
    public RefreshToken verificarToken(String token) {

        RefreshToken refreshToken = refreshTokenRepositorio.findByToken(token)
                .orElseThrow(() -> new MyException("Refresh token no válido",
                HttpStatus.BAD_REQUEST));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepositorio.delete(refreshToken);
            throw new MyException("Refresh token expirado",
                    HttpStatus.BAD_REQUEST);
        }

        return refreshToken;
    }

    @Transactional
    public void eliminarPorUsuario(Usuario usuario) {
        refreshTokenRepositorio.deleteByUsuario(usuario);
    }
}
