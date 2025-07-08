package com.example.PuntoVentaBack.ticketbase.control;

import com.example.PuntoVentaBack.ticketbase.model.TicketBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-base")
public class TicketBaseController {

    @Autowired
    private TicketBaseService service;

    @PostMapping
    public ResponseEntity<TicketBase> crear(@RequestBody TicketBase ticketBase) {
        return ResponseEntity.ok(service.guardar(ticketBase));
    }

    @GetMapping
    public ResponseEntity<List<TicketBase>> listar() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketBase> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/activo")
    public ResponseEntity<TicketBase> obtenerConfiguracionActiva() {
        return ResponseEntity.ok(service.obtenerConfiguracionActiva());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketBase> actualizar(
            @PathVariable Long id,
            @RequestBody TicketBase ticketActualizado) {
        return ResponseEntity.ok(service.actualizarConfiguracion(id, ticketActualizado));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<TicketBase> actualizarEstado(
            @PathVariable Long id,
            @RequestBody EstadoDTO estadoDTO) {
        TicketBase existente = service.obtenerPorId(id);
        existente.setHabilitado(estadoDTO.getHabilitado());
        return ResponseEntity.ok(service.guardar(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    public static class EstadoDTO {
        private Boolean habilitado;

        public Boolean getHabilitado() {
            return habilitado;
        }

        public void setHabilitado(Boolean habilitado) {
            this.habilitado = habilitado;
        }
    }
}