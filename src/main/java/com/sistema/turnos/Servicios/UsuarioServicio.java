package com.sistema.turnos.Servicios;

import com.sistema.turnos.DTO.Usuario.UsuarioRequestDTO;
import com.sistema.turnos.DTO.Usuario.UsuarioResponseDTO;
import com.sistema.turnos.Entidades.Usuario;
import com.sistema.turnos.Enum.Role;
import com.sistema.turnos.Repositorios.UsuarioRepositorio;
import com.sistema.turnos.errores.MyException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registrar(UsuarioRequestDTO dto) {
        validarPassword(dto.getPassword(), dto.getPassword2());
        validarEmailDisponible(dto.getEmail());

        Usuario u = new Usuario();

        u.setApellido(dto.getApellido());
        u.setEmail(dto.getEmail());
        u.setEnabled(true);
        u.setNombre(dto.getNombre());
        u.setRole(Role.USER);
        u.setPassword(passwordEncoder.encode(dto.getPassword()));

        usuarioRepositorio.save(u);

    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarUsuarios(Pageable pageable) {
        return usuarioRepositorio.findAll(pageable).map(this::toDTO);
    }

    public Usuario obtenerUsuarioLogueado() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new MyException("Usuario no autenticado",
                    HttpStatus.UNAUTHORIZED);
        }

        String email = auth.getName();

        Usuario usuario = usuarioRepositorio.findByEmail(email);

        if (usuario == null) {
            throw new MyException("Usuario no encontrado",
                    HttpStatus.NOT_FOUND);
        }

        if (!usuario.isEnabled()) {
            throw new MyException("Usuario deshabilitado",
                    HttpStatus.UNAUTHORIZED);
        }

        return usuario;
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorEmail(String email) {
        validarEmailFormato(email);
        Usuario usuario = usuarioRepositorio.findByEmail(email);
        if (usuario == null) {
            throw new MyException("Usuario no encontrado.", HttpStatus.NOT_FOUND);
        }
        return usuario;
    }

    private UsuarioResponseDTO toDTO(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRole());
        dto.setEnabled(u.isEnabled());
        dto.setId(u.getId());

        return dto;
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(UUID id) {
        return toDTO(buscarUsuarioPorId(id));
    }

    private Usuario buscarUsuarioPorId(UUID id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new MyException("Usuario no encontrado",
                HttpStatus.NOT_FOUND));

    }

    private void validarPassword(String password, String password2) {

        if (password == null || password.isBlank() || password.length() < 6) {
            throw new MyException(
                    "La contraseña debe tener al menos 6 caracteres",
                    HttpStatus.BAD_REQUEST);
        }

        if (!password.equals(password2)) {
            throw new MyException("Las contraseñas no coinciden",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void validarEmailDisponible(String email) {
        if (email == null || email.isBlank()) {
            throw new MyException("Email inválido",
                    HttpStatus.BAD_REQUEST);
        }

        if (usuarioRepositorio.findByEmail(email) != null) {
            throw new MyException("El email ya está en uso",
                    HttpStatus.CONFLICT);
        }
    }

    private void validarEmailFormato(String email) {
        if (email == null || email.isBlank()) {
            throw new MyException("Email inválido",
                    HttpStatus.BAD_REQUEST);
        }
    }

}
