package com.example.PuntoVentaBack.Pedido.control;

import com.example.PuntoVentaBack.Pedido.model.PedidoRepository;
import com.example.PuntoVentaBack.Pedido.dto.VentasPorProductoDiaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/ventas-por-producto-dia")
    public ResponseEntity<List<VentasPorProductoDiaDTO>> getVentasPorProductoDia(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<Object[]> resultados = pedidoRepository.findVentasPorProductoYDia(fechaInicio, fechaFin);
        List<VentasPorProductoDiaDTO> reporte = resultados.stream()
                .map(r -> new VentasPorProductoDiaDTO(
                        (Date) r[0],
                        (String) r[1],
                        (String) r[2],
                        ((Number) r[3]).longValue(),
                        (BigDecimal) r[4]))
                .collect(Collectors.toList());

        return ResponseEntity.ok(reporte);
    }
}