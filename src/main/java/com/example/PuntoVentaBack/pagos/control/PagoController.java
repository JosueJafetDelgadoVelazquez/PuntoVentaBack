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
            // Validación básica del DTO
            if (dto == null) {
                response.put("success", false);
                response.put("message", "El cuerpo de la petición no puede estar vacío");
                return ResponseEntity.badRequest().body(response);
            }

            // Establecer método de pago por defecto si no viene
            String metodoPago = (dto.getMetodoPago() != null && !dto.getMetodoPago().isEmpty())
                    ? dto.getMetodoPago().toUpperCase()
                    : "EFECTIVO";

            // Validar productos
            if (dto.getProductos() == null || dto.getProductos().isEmpty()) {
                response.put("success", false);
                response.put("message", "Debe incluir al menos un producto");
                return ResponseEntity.badRequest().body(response);
            }

            // Configurar el pago
            Pago pago = new Pago();
            pago.setMetodoPago(metodoPago);
            pago.setPermiteStockNegativo(dto.isPermiteStockNegativo() != null && dto.isPermiteStockNegativo());
            pago.setFechaHora(LocalDateTime.now());

            // Procesar cada producto
            List<Pedido> pedidos = dto.getProductos().stream().map(p -> {
                // Validar producto
                if (p.getProductoId() == null || p.getTalla() == null || p.getCantidad() <= 0) {
                    throw new RuntimeException("Datos del producto incompletos o inválidos");
                }

                Producto producto = productoRepository.findById(p.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + p.getProductoId()));

                // Buscar talla específica para el producto
                TallaConfiguracion talla = tallaConfiguracionRepository
                        .findByProductoIdAndTalla(producto.getId(), p.getTalla())
                        .orElseThrow(() -> new RuntimeException(
                                "Talla " + p.getTalla() + " no disponible para el producto: " + producto.getNombre()));

                // Validar stock
                int nuevoStock = producto.getStock() - p.getCantidad();
                if (!pago.isPermiteStockNegativo() && nuevoStock < 0) {
                    throw new RuntimeException(String.format(
                            "Stock insuficiente para %s (Talla %s). Stock actual: %d, se requieren: %d",
                            producto.getNombre(),
                            talla.getTalla(),
                            producto.getStock(),
                            p.getCantidad()));
                }

                // Actualizar stock
                producto.setStock(nuevoStock);
                productoRepository.save(producto);

                // Crear pedido
                Pedido pedido = new Pedido();
                pedido.setProducto(producto);
                pedido.setTallaConfiguracion(talla);
                pedido.setNombreProducto(producto.getNombre());
                pedido.setCantidad(p.getCantidad());
                pedido.setPagoProducto(p.getCantidad() * talla.getPrecio());
                pedido.setTalla(talla.getTalla());
                pedido.setPago(pago);

                return pedido;
            }).collect(Collectors.toList());

            // Calcular total
            double total = pedidos.stream()
                    .mapToDouble(Pedido::getPagoProducto)
                    .sum();
            pago.setTotalPagado(total);

            // Guardar en base de datos
            Pago pagoGuardado = pagoRepository.save(pago);
            pedidoRepository.saveAll(pedidos);

            // Preparar respuesta exitosa
            response.put("success", true);
            response.put("message", "Pago registrado exitosamente");
            response.put("data", pagoGuardado);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Errores de negocio conocidos
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            // Errores inesperados
            response.put("success", false);
            response.put("message", "Error interno al procesar el pago");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<Pago>> obtenerTodosLosPagos() {
        try {
            List<Pago> pagos = pagoRepository.findAll();
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPagoPorId(@PathVariable Long id) {
        try {
            return pagoRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}