package com.example.PuntoVentaBack.ticket.control;

import com.example.PuntoVentaBack.ticket.dto.TicketRequest;
import com.example.PuntoVentaBack.ticket.model.Ticket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearTicket(@RequestBody TicketRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Validación básica del request
            if (request == null) {
                response.put("success", false);
                response.put("message", "El cuerpo de la solicitud no puede estar vacío");
                return ResponseEntity.badRequest().body(response);
            }

            // Validar el request antes de procesarlo
            request.validar();

            Ticket ticket = ticketService.crearTicket(request);
            response.put("success", true);
            response.put("data", ticket);
            response.put("message", "Ticket creado exitosamente");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al procesar la solicitud: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerTicket(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Ticket ticket = ticketService.obtenerTicketPorId(id);
            response.put("success", true);
            response.put("data", ticket);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/por-pago/{pagoId}")
    public ResponseEntity<Map<String, Object>> obtenerTicketsPorPago(@PathVariable Long pagoId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Ticket> tickets = ticketService.obtenerTicketsPorPago(pagoId);
            response.put("success", true);
            response.put("data", tickets);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al obtener tickets: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/por-vendedor/{vendedorId}")
    public ResponseEntity<Map<String, Object>> obtenerTicketsPorVendedor(@PathVariable Long vendedorId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Ticket> tickets = ticketService.obtenerTicketsPorVendedor(vendedorId);
            response.put("success", true);
            response.put("data", tickets);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al obtener tickets: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}