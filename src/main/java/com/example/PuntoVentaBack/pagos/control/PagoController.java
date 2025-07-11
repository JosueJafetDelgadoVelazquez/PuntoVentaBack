package com.example.PuntoVentaBack.pagos.control;

import com.example.PuntoVentaBack.pagos.dto.PagoRequest;
import com.example.PuntoVentaBack.pagos.model.Pago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
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
}