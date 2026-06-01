package com.sistema.turnos.Controladores;

import com.sistema.turnos.DTO.Turno.TurnoRequestDTO;
import com.sistema.turnos.DTO.Turno.TurnoResponseDTO;
import com.sistema.turnos.Servicios.TurnoServicio;
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
@RequestMapping("/turnos")
@RequiredArgsConstructor
public class TurnoControlador {

    private final TurnoServicio turnoServicio;

    @PostMapping
    public ResponseEntity<TurnoResponseDTO> crear(
            @Valid @RequestBody TurnoRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(turnoServicio.reservarTurno(dto));

    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<Void> cancelar(
            @PathVariable("id") UUID id) {
        turnoServicio.cancelarTurno(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/confirmar/{id}")
    public ResponseEntity<Void> confirmar(
            @PathVariable("id") UUID id) {
        turnoServicio.confirmarTurno(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/rechazar/{id}")
    public ResponseEntity<Void> rechazar(
            @PathVariable("id") UUID id) {
        turnoServicio.rechazarTurno(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/completar/{id}")
    public ResponseEntity<Void> completar(
            @PathVariable("id") UUID id) {
        turnoServicio.completarTurno(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Page<TurnoResponseDTO>> me(Pageable pageable) {
        return ResponseEntity.ok(turnoServicio.misTurnos(pageable));
    }

    @GetMapping("/negocio")
    public ResponseEntity<Page<TurnoResponseDTO>> misTurnosNegocio(Pageable pageable) {
        return ResponseEntity.ok(turnoServicio.turnosMiNegocio(pageable));
    }

}
