package com.sistema.turnos.Controladores;

import com.sistema.turnos.DTO.Servicio.ServicioRequest;
import com.sistema.turnos.DTO.Servicio.ServicioResponse;
import com.sistema.turnos.Servicios.ServicioServicio;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicios")
@RequiredArgsConstructor
public class ServicioControlador {

    private final ServicioServicio servicioServicio;

    @PostMapping
    public ResponseEntity<ServicioResponse> upload(
            @Valid @RequestBody ServicioRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicioServicio.crearServicio(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponse> edit(@PathVariable("id") UUID id,
            @Valid @RequestBody ServicioRequest dto) {
        return ResponseEntity.ok(servicioServicio.editarServicio(id, dto));
    }

    @GetMapping("/negocio/{id}")
    public ResponseEntity<Page<ServicioResponse>> searchbyBusiness(
            @PathVariable("id") UUID idnegocio, Pageable pageable) {
        return ResponseEntity.ok(
                servicioServicio.searchbyBusiness(idnegocio, pageable));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Void> toggle(@PathVariable("id") UUID id) {
        servicioServicio.toggleActivoServicio(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponse> searchbyId(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(servicioServicio.searchbyId(id));
    }

}
