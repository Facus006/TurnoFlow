package com.sistema.turnos.Servicios;

import com.sistema.turnos.DTO.Negocio.NegocioResponse;
import com.sistema.turnos.DTO.Servicio.ServicioRequest;
import com.sistema.turnos.DTO.Servicio.ServicioResponse;
import com.sistema.turnos.Entidades.Negocio;
import com.sistema.turnos.Entidades.Servicio;
import com.sistema.turnos.Entidades.Usuario;
import com.sistema.turnos.Enum.Role;
import com.sistema.turnos.Repositorios.ServicioRepositorio;
import com.sistema.turnos.errores.MyException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServicioServicio {
    
    private final ServicioRepositorio servicioRepositorio;
    private final NegocioServicio negocioServicio;
    private final UsuarioServicio usuarioServicio;
    
    @Transactional
    public ServicioResponse crearServicio(ServicioRequest dto) {
        Negocio negocio = negocioServicio.obtenerMiNegocio();
        
        Servicio servicio = new Servicio();
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setDuracionMinutos(dto.getDuracionMinutos());
        servicio.setPrecio(dto.getPrecio());
        servicio.setNegocio(negocio);
        servicio.setActivo(true);
        servicioRepositorio.save(servicio);
        return toDto(servicio);
    }
    
    @Transactional
    public ServicioResponse editarServicio(UUID id, ServicioRequest dto) {
        Servicio servicio = servicioRepositorio.findById(id).orElseThrow(()
                -> new MyException("Servicio no encontrado",
                        HttpStatus.NOT_FOUND));
        Negocio negocio = negocioServicio.obtenerMiNegocio();
        if (!negocio.getId().equals(servicio.getNegocio().getId())) {
            throw new MyException("No tienes permisos para esta acción.", HttpStatus.FORBIDDEN);
        }
        
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setDuracionMinutos(dto.getDuracionMinutos());
        servicio.setPrecio(dto.getPrecio());
        servicioRepositorio.save(servicio);
        return toDto(servicio);
    }
    
    @Transactional
    public void toggleActivoServicio(UUID id) {
        Servicio servicio = servicioRepositorio.findById(id).orElseThrow(()
                -> new MyException("Servicio no encontrado",
                        HttpStatus.NOT_FOUND));
        Usuario usuario = usuarioServicio.obtenerUsuarioLogueado();
        if (usuario.getRole() == Role.ADMIN) {
            servicio.setActivo(!servicio.isActivo());
            servicioRepositorio.save(servicio);
            return;
        }
        
        Negocio negocio = negocioServicio.obtenerMiNegocio();
        
        if (!negocio.getId().equals(servicio.getNegocio().getId())) {
            throw new MyException("No tienes permisos para esta acción.", HttpStatus.FORBIDDEN);
        }
        
        servicio.setActivo(!servicio.isActivo());
        servicioRepositorio.save(servicio);
    }
    
    @Transactional(readOnly = true)
    public Page<ServicioResponse> searchbyBusiness(UUID id, Pageable pageable) {
        return servicioRepositorio.findByNegocioId(id, pageable)
                .map(this::toDto);
    }
    
    @Transactional(readOnly = true)
    public ServicioResponse searchbyId(UUID id) {
        return toDto(servicioRepositorio.findById(id).orElseThrow(()
                -> new MyException("Servicio no encontrado",
                        HttpStatus.NOT_FOUND)));
    }
    
    public ServicioResponse toDto(Servicio servicio) {
        ServicioResponse dto = new ServicioResponse();
        dto.setId(servicio.getId());
        dto.setActivo(servicio.isActivo());
        dto.setNombre(servicio.getNombre());
        dto.setDescripcion(servicio.getDescripcion());
        dto.setDuracionMinutos(servicio.getDuracionMinutos());
        dto.setNegocioId(servicio.getNegocio().getId());
        dto.setNegocioNombre(servicio.getNegocio().getNombre());
        dto.setPrecio(servicio.getPrecio());
        return dto;
    }
}
