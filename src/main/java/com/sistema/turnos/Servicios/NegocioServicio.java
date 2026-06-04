package com.sistema.turnos.Servicios;

import com.sistema.turnos.DTO.Negocio.NegocioRequest;
import com.sistema.turnos.DTO.Negocio.NegocioResponse;
import com.sistema.turnos.Entidades.Negocio;
import com.sistema.turnos.Entidades.Usuario;
import com.sistema.turnos.Enum.Role;
import com.sistema.turnos.Repositorios.NegocioRepositorio;
import com.sistema.turnos.errores.MyException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NegocioServicio {

    private final UsuarioServicio usuarioServicio;
    private final NegocioRepositorio negocioRepositorio;

    @Transactional
    public NegocioResponse crearNegocio(NegocioRequest dto) {

        Usuario logueado = usuarioServicio.obtenerUsuarioLogueado();
        if (logueado.getRole() != Role.NEGOCIO) {
            throw new MyException("Solo los negocios pueden realizar esta acción", HttpStatus.FORBIDDEN);
        }
        if (negocioRepositorio.existsByPropietarioId(logueado.getId())) {
            throw new MyException("Ya tenés un negocio creado", HttpStatus.CONFLICT);
        }
        Negocio negocio = new Negocio();
        negocio.setNombre(dto.getNombre());
        negocio.setDescripcion(dto.getDescripcion());
        negocio.setCategoria(dto.getCategoria());
        negocio.setDireccion(dto.getDireccion());
        negocio.setTelefono(dto.getTelefono());
        negocio.setActivo(true);
        negocio.setPropietario(logueado);

        negocioRepositorio.save(negocio);
        return (toDto(negocio));
    }

    @Transactional
    public NegocioResponse editarNegocio(UUID idnegocio, NegocioRequest dto) {
        Negocio negocio = negocioRepositorio.findById(idnegocio).orElseThrow(()
                -> new MyException("Negocio no encontrado",
                        HttpStatus.NOT_FOUND));

        Usuario logueado = usuarioServicio.obtenerUsuarioLogueado();
        if (!negocio.getPropietario().getId().equals(logueado.getId())) {
            throw new MyException("No tienes permisos para esta acción.", HttpStatus.FORBIDDEN);
        }

        negocio.setNombre(dto.getNombre());
        negocio.setDescripcion(dto.getDescripcion());
        negocio.setCategoria(dto.getCategoria());
        negocio.setDireccion(dto.getDireccion());
        negocio.setTelefono(dto.getTelefono());

        negocioRepositorio.save(negocio);
        return (toDto(negocio));
    }

    @Transactional
    public void toggleActivoNegocio(UUID id) {
        Negocio negocio = negocioRepositorio.findById(id).orElseThrow(()
                -> new MyException("Negocio no encontrado",
                        HttpStatus.NOT_FOUND));
        Usuario logueado = usuarioServicio.obtenerUsuarioLogueado();
        if (logueado.getRole() != Role.ADMIN
                && !negocio.getPropietario().getId().equals(logueado.getId())) {

            throw new MyException("No tienes permisos para esta acción.",
                    HttpStatus.FORBIDDEN
            );
        }

        negocio.setActivo(!negocio.isActivo());
        negocioRepositorio.save(negocio);
    }

    @Transactional(readOnly = true)
    public Page<NegocioResponse> findAlldto(Pageable pageable) {
        return negocioRepositorio.findByActivoTrue(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<NegocioResponse> findAll(Pageable pageable) {
        return negocioRepositorio.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public NegocioResponse findbyId(UUID id) {
        return (toDto(negocioRepositorio.findById(id).orElseThrow(()
                -> new MyException("Negocio no encontrado",
                        HttpStatus.NOT_FOUND))));
    }

    public Negocio obtenerMiNegocio() {
        Usuario logueado = usuarioServicio.obtenerUsuarioLogueado();
        return (negocioRepositorio.findByPropietarioId(logueado.getId())
                .orElseThrow(() -> new MyException("No tenés un negocio creado", HttpStatus.NOT_FOUND)));
    }

    public NegocioResponse toDto(Negocio negocio) {
        NegocioResponse dto = new NegocioResponse();

        dto.setId(negocio.getId());
        dto.setActivo(negocio.isActivo());
        dto.setNombre(negocio.getNombre());
        dto.setDescripcion(negocio.getDescripcion());
        dto.setCategoria(negocio.getCategoria());
        dto.setDireccion(negocio.getDireccion());
        dto.setTelefono(negocio.getTelefono());
        dto.setPropietarioId(negocio.getPropietario().getId());
        dto.setPropietarioNombre(negocio.getPropietario().getNombre());

        return dto;
    }

    @Transactional(readOnly = true)
    public NegocioResponse obtenerMiNegocioDto() {
        return toDto(obtenerMiNegocio());
    }

}
