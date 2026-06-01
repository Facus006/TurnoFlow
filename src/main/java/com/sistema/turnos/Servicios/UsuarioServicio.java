package com.sistema.turnos.Servicios;

import com.sistema.turnos.DTO.Usuario.ActualizarEmailDTO;
import com.sistema.turnos.DTO.Usuario.EditarPasswordDTO;
import com.sistema.turnos.DTO.Usuario.EditarUsuarioDTO;
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
        validarAdmin(obtenerUsuarioLogueado());
        return usuarioRepositorio.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarUsuariosActivos(Pageable pageable) {
        return usuarioRepositorio.findByEnabledTrue(pageable).map(this::toDTO);
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

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> buscarUsuarioPorNombre(String nombre,
            Pageable pageable) {
        validarStringFormato(nombre);
        return usuarioRepositorio
                .findByNombreContainingIgnoreCase(nombre, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(UUID id) {
        return toDTO(buscarUsuarioPorId(id));
    }

    @Transactional
    public void eliminarUsuarioPorId(UUID id) {
        validarId(id);

        Usuario logeado = obtenerUsuarioLogueado();
        Usuario usuario = buscarUsuarioPorId(id);

        validarPropietarioOAdmin(logeado, usuario);

        usuarioRepositorio.delete(usuario);

    }

    @Transactional
    public UsuarioResponseDTO editarUsuario(EditarUsuarioDTO dto) {
        Usuario logeado = obtenerUsuarioLogueado();
        logeado.setNombre(dto.getNombre());
        logeado.setApellido(dto.getApellido());
        usuarioRepositorio.save(logeado);
        return toDTO(logeado);
    }

    @Transactional
    public Usuario editarPassword(EditarPasswordDTO dto) {

        validarStringFormato(dto.getPassword());
        validarPassword(dto.getNuevoPassword(), dto.getNuevoPassword2());

        Usuario u = obtenerUsuarioLogueado();

        if (!passwordEncoder.matches(dto.getPassword(), u.getPassword())) {
            throw new MyException("Contraseña incorrecta.",
                    HttpStatus.BAD_REQUEST);
        }

        u.setPassword(passwordEncoder.encode(dto.getNuevoPassword()));

        return usuarioRepositorio.save(u);
    }

    @Transactional
    public void alternarRolAdmin(UUID id) {
        validarId(id);

        Usuario logeado = obtenerUsuarioLogueado();

        validarAdmin(logeado);

        Usuario u = buscarUsuarioPorId(id);

        if (logeado.getId().equals(u.getId())) {
            throw new MyException("No puedes cambiar tu propio rol",
                    HttpStatus.BAD_REQUEST);
        }

        if (u.getRole() == Role.NEGOCIO) {
            throw new MyException("No puedes cambiar el rol de un negocio desde aquí",
                    HttpStatus.BAD_REQUEST);
        }

        if (u.getRole() == Role.ADMIN) {
            u.setRole(Role.USER);
        } else {
            u.setRole(Role.ADMIN);
        }

        usuarioRepositorio.save(u);
    }

    @Transactional
    public void setNegocio(UUID id) {
        validarId(id);
        Usuario logeado = obtenerUsuarioLogueado();
        validarAdmin(logeado);
        Usuario u = buscarUsuarioPorId(id);
        if (logeado.getId().equals(u.getId())) {
            throw new MyException("No puedes cambiar tu propio rol",
                    HttpStatus.BAD_REQUEST);
        }
        u.setRole(Role.NEGOCIO);
        usuarioRepositorio.save(u);
    }

    @Transactional
    public void quitarRolNegocio(UUID id) {
        validarId(id);
        Usuario logeado = obtenerUsuarioLogueado();
        validarAdmin(logeado);
        Usuario u = buscarUsuarioPorId(id);
        if (logeado.getId().equals(u.getId())) {
            throw new MyException("No puedes cambiar tu propio rol",
                    HttpStatus.BAD_REQUEST);
        }
        u.setRole(Role.USER);
        usuarioRepositorio.save(u);
    }

    @Transactional
    public void cambiarEstadoUsuario(UUID id) {
        validarId(id);
        Usuario u = buscarUsuarioPorId(id);
        Usuario logeado = obtenerUsuarioLogueado();
        validarAdmin(logeado);
        u.setEnabled(!u.isEnabled());
        usuarioRepositorio.save(u);
    }

    @Transactional
    public Usuario actualizarEmail(ActualizarEmailDTO dto) {
        validarEmailFormato(dto.getEmail());
        validarStringFormato(dto.getPassword());

        Usuario u = obtenerUsuarioLogueado();
        if (!passwordEncoder.matches(dto.getPassword(), u.getPassword())) {
            throw new MyException("Contraseña incorrecta.",
                    HttpStatus.BAD_REQUEST);
        }
        if (u.getEmail().equalsIgnoreCase(dto.getEmail())) {
            throw new MyException("El nuevo email no puede ser igual al actual.",
                    HttpStatus.CONFLICT);
        }

        validarEmailDisponible(dto.getEmail());
        u.setEmail(dto.getEmail());

        return usuarioRepositorio.save(u);
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

    private void validarId(UUID id) {
        if (id == null) {
            throw new MyException("ID inválido",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void validarPropietarioOAdmin(Usuario logeado, Usuario objetivo) {
        if (!logeado.getId().equals(objetivo.getId())
                && logeado.getRole() != Role.ADMIN) {
            throw new MyException("No tienes permisos para esta acción.",
                    HttpStatus.FORBIDDEN);
        }
    }

    private void validarAdmin(Usuario usuario) {
        if (usuario.getRole() != Role.ADMIN) {
            throw new MyException("No tienes permiso para esta acción",
                    HttpStatus.FORBIDDEN);
        }
    }

    private void validarStringFormato(String string) {
        if (string == null || string.isBlank()) {
            throw new MyException("Error texto no valido.",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
