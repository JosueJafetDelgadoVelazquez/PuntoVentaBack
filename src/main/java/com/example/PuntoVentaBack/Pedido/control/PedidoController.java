package com.example.PuntoVentaBack.Pedido.control;

import com.example.PuntoVentaBack.Pedido.dto.VentasPorDiaDTO;
import com.example.PuntoVentaBack.Pedido.dto.VentasPorProductoDTO;
import com.example.PuntoVentaBack.Pedido.model.Pedido;
import com.example.PuntoVentaBack.Pedido.dto.RegistroMultiplePedidosDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/registrar-multiple")
    public ResponseEntity<?> registrarPedidosMultiple(@RequestBody RegistroMultiplePedidosDTO registroDTO) {
        try {
            List<Pedido> pedidosRegistrados = pedidoService.registrarPedidosMultiple(
                    registroDTO.getPagoId(),
                    registroDTO.getProductos()
            );
            return ResponseEntity.ok(pedidosRegistrados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar pedidos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedidoPorId(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.obtenerPedidoPorId(id);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/por-pago/{pagoId}")
    public ResponseEntity<List<Pedido>> obtenerPedidosPorPago(@PathVariable Long pagoId) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorPago(pagoId));
    }

    @GetMapping("/por-producto/{productoId}")
    public ResponseEntity<List<Pedido>> obtenerPedidosPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorProducto(productoId));
    }

    @GetMapping("/reporte/ventas-producto")
    public ResponseEntity<List<VentasPorProductoDTO>> obtenerVentasPorProducto(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(pedidoService.obtenerVentasPorProducto(fechaInicio, fechaFin));
    }

    @GetMapping("/reporte/ventas-dia")
    public ResponseEntity<List<VentasPorDiaDTO>> obtenerVentasPorDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(pedidoService.obtenerVentasPorDia(fechaInicio, fechaFin));
    }

    @GetMapping("/reporte/completo")
    public ResponseEntity<Map<String, Object>> obtenerReporteCompleto(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("ventasPorDia", pedidoService.obtenerVentasPorDia(fechaInicio, fechaFin));
        reporte.put("ventasPorProducto", pedidoService.obtenerVentasPorProducto(fechaInicio, fechaFin));

        return ResponseEntity.ok(reporte);
    }

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido) {
        try {
            Pedido nuevoPedido = pedidoService.crearPedido(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizarPedido(@PathVariable Long id, @RequestBody Pedido pedido) {
        try {
            Pedido pedidoActualizado = pedidoService.actualizarPedido(id, pedido);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        try {
            pedidoService.eliminarPedido(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}