package com.example.PuntoVentaBack.pagos.control;

import com.example.PuntoVentaBack.pagos.dto.PagoRequest;
import com.example.PuntoVentaBack.pagos.model.Pago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    @Autowired
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarPago(@RequestBody PagoRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            request.setPermiteStockNegativo(true);
            Pago pago = pagoService.procesarPago(request);

            response.put("success", true);
            response.put("message", "Pago registrado exitosamente");
            response.put("data", pago);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al procesar el pago");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/por-fecha")
    public ResponseEntity<List<Pago>> getPagosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(pagoService.buscarPagosPorRangoFechas(fechaInicio, fechaFin));
    }

    @GetMapping("/reporte")
    public ResponseEntity<Map<String, Object>> getReporteMetodosPago(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validar fechas
            if (fechaInicio.isAfter(fechaFin)) {
                response.put("success", false);
                response.put("message", "La fecha de inicio no puede ser posterior a la fecha fin");
                return ResponseEntity.badRequest().body(response);
            }

            List<Pago> pagos = pagoService.buscarPagosPorRangoFechas(fechaInicio, fechaFin);

            if (pagos.isEmpty()) {
                response.put("success", true);
                response.put("message", "No hay datos para el período seleccionado");
                response.put("data", Collections.emptyMap());
                return ResponseEntity.ok(response);
            }

            Map<String, Object> reporte = pagoService.generarReporteMetodosPago(pagos);

            response.put("success", true);
            response.put("message", "Reporte generado exitosamente");
            response.put("data", reporte);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al generar el reporte: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}