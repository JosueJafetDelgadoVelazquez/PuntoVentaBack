package com.example.PuntoVentaBack.Pedido.control;

import com.example.PuntoVentaBack.Pedido.model.Pedido;
import com.example.PuntoVentaBack.Pedido.dto.RegistroMultiplePedidosDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorPago(pagoId);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/por-producto/{productoId}")
    public ResponseEntity<List<Pedido>> obtenerPedidosPorProducto(@PathVariable Long productoId) {
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorProducto(productoId);
        return ResponseEntity.ok(pedidos);
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