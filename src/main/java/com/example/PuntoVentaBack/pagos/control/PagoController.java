package com.example.PuntoVentaBack.pagos.control;

import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.inventory.model.ProductoRepository;
import com.example.PuntoVentaBack.pagos.dto.PagoDTO;
import com.example.PuntoVentaBack.pagos.model.Pago;
import com.example.PuntoVentaBack.pagos.model.PagoRepository;
import com.example.PuntoVentaBack.Pedido.model.Pedido;
import com.example.PuntoVentaBack.Pedido.model.PedidoRepository;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TallaConfiguracionRepository tallaConfiguracionRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarPago(@RequestBody PagoDTO dto) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validación del método de pago
            String metodoPago = (dto.getMetodo_pago() != null && !dto.getMetodo_pago().isEmpty())
                    ? dto.getMetodo_pago().toUpperCase()
                    : "EFECTIVO";

            // Validación de productos
            if (dto.getProductos() == null || dto.getProductos().isEmpty()) {
                response.put("success", false);
                response.put("message", "Debe incluir al menos un producto");
                return ResponseEntity.badRequest().body(response);
            }

            // Crear el pago
            Pago pago = new Pago();
            pago.setMetodoPago(metodoPago);
            pago.setFechaHora(LocalDateTime.now());

            // Procesar productos y calcular total
            List<Pedido> pedidos = dto.getProductos().stream().map(p -> {
                Producto producto = productoRepository.findById(p.getIdProducto())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

                TallaConfiguracion talla = tallaConfiguracionRepository.findById(p.getIdTallaConfiguracion())
                        .orElseThrow(() -> new RuntimeException("Talla no encontrada"));

                // Validar stock
                if (producto.getStock() < p.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
                }

                // Actualizar stock
                producto.setStock(producto.getStock() - p.getCantidad());
                productoRepository.save(producto);

                // Crear pedido
                Pedido pedido = new Pedido();
                pedido.setProducto(producto);
                pedido.setTallaConfiguracion(talla);
                pedido.setNombreProducto(p.getNombreProducto());
                pedido.setCantidad(p.getCantidad());
                pedido.setPagoProducto(p.getCantidad() * talla.getPrecio());
                pedido.setPago(pago);

                return pedido;
            }).collect(Collectors.toList());

            // Calcular total
            double total = pedidos.stream()
                    .mapToDouble(Pedido::getPagoProducto)
                    .sum();
            pago.setTotalPagado(total);

            // Guardar
            Pago pagoGuardado = pagoRepository.save(pago);
            pedidoRepository.saveAll(pedidos);

            // Respuesta exitosa
            response.put("success", true);
            response.put("message", "Pago registrado exitosamente");
            response.put("data", pagoGuardado);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al registrar el pago: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<Pago>> obtenerTodosLosPagos() {
        return ResponseEntity.ok(pagoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPagoPorId(@PathVariable Long id) {
        return pagoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}