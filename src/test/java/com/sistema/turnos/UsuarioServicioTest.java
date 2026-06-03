package com.sistema.turnos;

import com.sistema.turnos.DTO.Usuario.UsuarioRequestDTO;
import com.sistema.turnos.Entidades.Usuario;
import com.sistema.turnos.Repositorios.UsuarioRepositorio;
import com.sistema.turnos.Servicios.UsuarioServicio;
import com.sistema.turnos.errores.MyException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServicioTest {

    @Mock
    private UsuarioRepositorio usuarioRepositorio;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServicio usuarioServicio;

    @Test
    void registrar_deberiaLanzarExcepcion_siEmailYaExiste() {

        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setEmail("juan@email.com");
        dto.setPassword("123456");
        dto.setPassword2("123456");
        dto.setNombre("Juan");
        dto.setApellido("Pérez");

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail("juan@email.com");

        when(usuarioRepositorio.findByEmail("juan@email.com"))
                .thenReturn(usuarioExistente);

        MyException ex = assertThrows(MyException.class, () -> {
            usuarioServicio.registrar(dto);
        });

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void registrar_deberiaGuardarUsuario_siDatosSonValidos() {

        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setEmail("juan@email.com");
        dto.setPassword("123456");
        dto.setPassword2("123456");
        dto.setNombre("Juan");
        dto.setApellido("Pérez");

        when(usuarioRepositorio.findByEmail("juan@email.com"))
                .thenReturn(null); // el email no existe

        when(passwordEncoder.encode("123456"))
                .thenReturn("hasheado123");

        when(usuarioRepositorio.save(any(Usuario.class)))
                .thenReturn(new Usuario());

        usuarioServicio.registrar(dto);

        verify(usuarioRepositorio, times(1)).save(any(Usuario.class));
    }

    @Test
    void registrar_deberiaLanzarExcepcion_siPasswordsNoCoinciden() {

        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setEmail("juan@email.com");
        dto.setPassword("123456");
        dto.setPassword2("999999"); // distinta
        dto.setNombre("Juan");
        dto.setApellido("Pérez");

        MyException ex = assertThrows(MyException.class, () -> {
            usuarioServicio.registrar(dto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());

        verify(usuarioRepositorio, never()).save(any());
    }

}
