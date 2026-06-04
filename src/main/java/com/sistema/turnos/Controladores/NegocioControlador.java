package com.sistema.turnos.Controladores;

import com.sistema.turnos.DTO.Negocio.NegocioRequest;
import com.sistema.turnos.DTO.Negocio.NegocioResponse;
import com.sistema.turnos.Servicios.NegocioServicio;
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
@RequestMapping("/negocios")
@RequiredArgsConstructor
public class NegocioControlador {

    private final NegocioServicio negocioServicio;

    @PostMapping
    public ResponseEntity<NegocioResponse> upload(
            @Valid @RequestBody NegocioRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(negocioServicio.crearNegocio(dto));
    }

    @GetMapping()
    public ResponseEntity<Page<NegocioResponse>> all(Pageable pageable) {
        return ResponseEntity.ok(negocioServicio.findAlldto(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NegocioResponse> edit(@PathVariable("id") UUID id,
            @Valid @RequestBody NegocioRequest dto) {
        return ResponseEntity.ok(negocioServicio.editarNegocio(id, dto));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Void> toggle(@PathVariable("id") UUID id) {
        negocioServicio.toggleActivoNegocio(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NegocioResponse> getOne(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(negocioServicio.findbyId(id));
    }

    @GetMapping("/mi-negocio")
    public ResponseEntity<NegocioResponse> mybusiness() {
        return ResponseEntity.ok(negocioServicio.obtenerMiNegocioDto());
    }

}
