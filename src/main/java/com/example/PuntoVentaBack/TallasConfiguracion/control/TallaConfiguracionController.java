package com.example.PuntoVentaBack.TallasConfiguracion.control;

import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracionRepository;
import com.example.PuntoVentaBack.inventory.dto.TallaConfiguracionDTO;
import com.example.PuntoVentaBack.inventory.control.ProductoService;
import com.example.PuntoVentaBack.inventory.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tallas-configuracion")
public class TallaConfiguracionController {

    @Autowired
    private TallaConfiguracionRepository repository;

    @Autowired
    private ProductoService productoService;

    @PutMapping("/actualizar-masivo/{productoId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> actualizarTallasProducto(
            @PathVariable Long productoId,
            @RequestBody List<TallaConfiguracionDTO> tallasRequest) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Validar que el producto exista
            Producto producto = productoService.obtenerProductoPorId(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            // 2. Obtener tallas existentes para este producto
            List<TallaConfiguracion> tallasExistentes = repository.findByProductoId(productoId);

            // 3. Procesar cada talla recibida
            List<TallaConfiguracion> tallasActualizadas = new ArrayList<>();

            for (TallaConfiguracionDTO tallaRequest : tallasRequest) {
                // Validar datos básicos
                if (tallaRequest.getTalla() == null || tallaRequest.getTalla().isEmpty()) {
                    throw new IllegalArgumentException("El nombre de la talla es requerido");
                }
                if (tallaRequest.getPrecio() <= 0) {
                    throw new IllegalArgumentException("El precio debe ser mayor que cero");
                }
                if (tallaRequest.getStock() < 0) {
                    throw new IllegalArgumentException("El stock no puede ser negativo");
                }

                TallaConfiguracion talla = tallasExistentes.stream()
                        .filter(t -> t.getTalla().equals(tallaRequest.getTalla()))
                        .findFirst()
                        .orElse(new TallaConfiguracion());

                talla.setTalla(tallaRequest.getTalla());
                talla.setPrecio(tallaRequest.getPrecio());
                talla.setStock(tallaRequest.getStock());
                talla.setProducto(producto);

                if (producto.getTallasCategoria() != null) {
                    talla.setTallaCategoria(producto.getTallasCategoria());
                }

                tallasActualizadas.add(repository.save(talla));
            }

            // 4. Eliminar tallas que ya no están en la lista
            List<String> tallasActualesNombres = tallasRequest.stream()
                    .map(TallaConfiguracionDTO::getTalla)
                    .collect(Collectors.toList());

            List<TallaConfiguracion> tallasAEliminar = tallasExistentes.stream()
                    .filter(t -> !tallasActualesNombres.contains(t.getTalla()))
                    .collect(Collectors.toList());

            if (!tallasAEliminar.isEmpty()) {
                repository.deleteAll(tallasAEliminar);
            }

            // 5. Actualizar stock total del producto
            productoService.actualizarStockProducto(productoId);

            // 6. Preparar respuesta
            List<Map<String, Object>> tallasResponse = tallasActualizadas.stream()
                    .map(t -> {
                        Map<String, Object> tallaMap = new HashMap<>();
                        tallaMap.put("id", t.getId());
                        tallaMap.put("talla", t.getTalla());
                        tallaMap.put("precio", t.getPrecio());
                        tallaMap.put("stock", t.getStock());
                        tallaMap.put("productoId", t.getProducto().getId());
                        return tallaMap;
                    })
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("data", tallasResponse);
            response.put("message", "Tallas actualizadas correctamente");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar tallas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearTallaConfiguracion(@RequestBody TallaConfiguracionDTO configuracion) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Validaciones
            if (configuracion.getTalla() == null || configuracion.getTalla().isEmpty()) {
                response.put("success", false);
                response.put("message", "La talla es obligatoria");
                return ResponseEntity.badRequest().body(response);
            }
            if (configuracion.getPrecio() <= 0) {
                response.put("success", false);
                response.put("message", "El precio debe ser mayor que cero");
                return ResponseEntity.badRequest().body(response);
            }
            if (configuracion.getProductoId() == null) {
                response.put("success", false);
                response.put("message", "El producto es obligatorio");
                return ResponseEntity.badRequest().body(response);
            }
            if (configuracion.getStock() < 0) {
                response.put("success", false);
                response.put("message", "El stock no puede ser negativo");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar si ya existe la talla para el producto
            if (repository.existsByProductoIdAndTalla(configuracion.getProductoId(), configuracion.getTalla())) {
                response.put("success", false);
                response.put("message", "Ya existe esta talla para el producto");
                return ResponseEntity.badRequest().body(response);
            }

            Producto producto = productoService.obtenerProductoPorId(configuracion.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            TallaConfiguracion nuevaConfiguracion = new TallaConfiguracion();
            nuevaConfiguracion.setTalla(configuracion.getTalla());
            nuevaConfiguracion.setPrecio(configuracion.getPrecio());
            nuevaConfiguracion.setStock(configuracion.getStock());
            nuevaConfiguracion.setProducto(producto);

            if (producto.getTallasCategoria() != null) {
                nuevaConfiguracion.setTallaCategoria(producto.getTallasCategoria());
            }

            TallaConfiguracion saved = repository.save(nuevaConfiguracion);
            productoService.actualizarStockProducto(configuracion.getProductoId());

            response.put("success", true);
            response.put("data", toTallaResponse(saved));
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar la configuración: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/por-producto/{productoId}")
    public ResponseEntity<Map<String, Object>> obtenerTallasPorProducto(@PathVariable Long productoId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<TallaConfiguracion> tallas = repository.findByProductoId(productoId);

            List<Map<String, Object>> tallasResponse = tallas.stream()
                    .map(this::toTallaResponse)
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("data", tallasResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener tallas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarTallaConfiguracion(
            @PathVariable Long id,
            @RequestBody TallaConfiguracionDTO configuracion) {
        Map<String, Object> response = new HashMap<>();
        try {
            TallaConfiguracion existente = repository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Configuración de talla no encontrada"));

            // Validar que no exista otra talla con el mismo nombre para el producto
            if (!existente.getTalla().equals(configuracion.getTalla()) &&
                    repository.existsByProductoIdAndTalla(existente.getProducto().getId(), configuracion.getTalla())) {
                throw new IllegalArgumentException("Ya existe esta talla para el producto");
            }

            existente.setTalla(configuracion.getTalla());
            existente.setPrecio(configuracion.getPrecio());
            existente.setStock(configuracion.getStock());

            TallaConfiguracion updated = repository.save(existente);
            productoService.actualizarStockProducto(updated.getProducto().getId());

            response.put("success", true);
            response.put("data", toTallaResponse(updated));
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarTallaConfiguracion(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            TallaConfiguracion configuracion = repository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Configuración de talla no encontrada"));

            Long productoId = configuracion.getProducto().getId();
            repository.deleteById(id);
            productoService.actualizarStockProducto(productoId);

            response.put("success", true);
            response.put("message", "Talla eliminada correctamente");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Métodos auxiliares
    private Map<String, Object> toTallaResponse(TallaConfiguracion talla) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", talla.getId());
        response.put("talla", talla.getTalla());
        response.put("precio", talla.getPrecio());
        response.put("stock", talla.getStock());
        response.put("productoId", talla.getProducto().getId());
        if (talla.getTallaCategoria() != null) {
            response.put("tallaCategoriaId", talla.getTallaCategoria().getId());
        }
        return response;
    }
}