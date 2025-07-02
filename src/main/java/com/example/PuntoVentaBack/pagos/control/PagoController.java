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
            // 1. Validación básica del DTO
            if (dto == null) {
                return buildErrorResponse(response, "El cuerpo de la petición no puede estar vacío");
            }

            // 2. Validación de productos
            if (dto.getProductos() == null || dto.getProductos().isEmpty()) {
                return buildErrorResponse(response, "Debe incluir al menos un producto");
            }

            // 3. Validar cada producto individualmente
            for (PagoDTO.ProductoPagoDTO productoDTO : dto.getProductos()) {
                if (productoDTO.getProductoId() == null ||
                        productoDTO.getTalla() == null ||
                        productoDTO.getCantidad() <= 0) {
                    return buildErrorResponse(response, "Datos del producto incompletos o inválidos");
                }
            }

            // 4. Configurar el pago
            Pago pago = new Pago();
            pago.setMetodoPago(dto.getMetodoPago() != null ? dto.getMetodoPago().toUpperCase() : "EFECTIVO");
            pago.setPermiteStockNegativo(dto.isPermiteStockNegativo() != null && dto.isPermiteStockNegativo());
            pago.setFechaHora(LocalDateTime.now());

            // 5. Procesar cada producto
            List<Pedido> pedidos = dto.getProductos().stream().map(p -> {
                Producto producto = productoRepository.findById(p.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + p.getProductoId()));

                // Buscar talla específica para el producto
                TallaConfiguracion talla = tallaConfiguracionRepository
                        .findByProductoIdAndTalla(producto.getId(), p.getTalla())
                        .orElseThrow(() -> new RuntimeException(
                                "Talla " + p.getTalla() + " no disponible para el producto: " + producto.getNombre()));

                // Validar stock
                int nuevoStock = talla.getStock() - p.getCantidad();
                if (!pago.isPermiteStockNegativo() && nuevoStock < 0) {
                    throw new RuntimeException(String.format(
                            "Stock insuficiente para %s (Talla %s). Stock actual: %d, se requieren: %d",
                            producto.getNombre(),
                            talla.getTalla(),
                            talla.getStock(),
                            p.getCantidad()));
                }

                // Actualizar stock de la talla específica
                talla.setStock(nuevoStock);
                tallaConfiguracionRepository.save(talla);

                // Actualizar stock general del producto
                actualizarStockProducto(producto);

                // Crear pedido
                Pedido pedido = new Pedido();
                pedido.setProducto(producto);
                pedido.setTallaConfiguracion(talla);
                pedido.setNombreProducto(producto.getNombre());
                pedido.setCantidad(p.getCantidad());
                pedido.setPagoProducto(p.getCantidad() * p.getPrecio());
                pedido.setTalla(talla.getTalla());
                pedido.setPago(pago);

                return pedido;
            }).collect(Collectors.toList());

            // 6. Calcular total (usamos el total del DTO o calculamos)
            double total = dto.getTotal() > 0 ? dto.getTotal() :
                    pedidos.stream()
                            .mapToDouble(Pedido::getPagoProducto)
                            .sum();
            pago.setTotalPagado(total);

            // 7. Guardar en base de datos
            Pago pagoGuardado = pagoRepository.save(pago);
            pedidoRepository.saveAll(pedidos);

            // 8. Preparar respuesta exitosa
            return buildSuccessResponse(response, pagoGuardado);

        } catch (RuntimeException e) {
            return buildErrorResponse(response, e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(response, "Error interno al procesar el pago");
        }
    }

    private void actualizarStockProducto(Producto producto) {
        if (producto.getTallasConfiguracion() != null) {
            int stockTotal = producto.getTallasConfiguracion()
                    .stream()
                    .mapToInt(TallaConfiguracion::getStock)
                    .sum();
            producto.setStock(stockTotal);
            productoRepository.save(producto);
        }
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(Map<String, Object> response, String message) {
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<Map<String, Object>> buildSuccessResponse(Map<String, Object> response, Pago pago) {
        response.put("success", true);
        response.put("message", "Pago registrado exitosamente");
        response.put("data", pago);
        return ResponseEntity.ok(response);
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