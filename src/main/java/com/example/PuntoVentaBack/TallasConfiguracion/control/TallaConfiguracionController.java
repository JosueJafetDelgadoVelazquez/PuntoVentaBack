package com.example.PuntoVentaBack.TallasConfiguracion.control;

import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracionRepository;
import com.example.PuntoVentaBack.inventory.control.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tallas-configuracion")
public class TallaConfiguracionController {

    @Autowired
    private TallaConfiguracionRepository repository;

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearTallaConfiguracion(@RequestBody TallaConfiguracion configuracion) {
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
            if (configuracion.getProducto() == null || configuracion.getProducto().getId() == null) {
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
            if (repository.existsByProductoIdAndTalla(configuracion.getProducto().getId(), configuracion.getTalla())) {
                response.put("success", false);
                response.put("message", "Ya existe esta talla para el producto");
                return ResponseEntity.badRequest().body(response);
            }

            TallaConfiguracion nuevaConfiguracion = repository.save(configuracion);
            productoService.actualizarStockProducto(configuracion.getProducto().getId());

            response.put("success", true);
            response.put("data", nuevaConfiguracion);
            return ResponseEntity.ok(response);

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

            if (tallas == null || tallas.isEmpty()) {
                response.put("success", true);
                response.put("data", Collections.emptyList());
                return ResponseEntity.ok(response);
            }

            // Convertir a lista de mapas con información relevante
            List<Map<String, Object>> tallasResponse = tallas.stream()
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
            @RequestBody TallaConfiguracion configuracion) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<TallaConfiguracion> existente = repository.findById(id);
            if (existente.isEmpty()) {
                response.put("success", false);
                response.put("message", "Configuración de talla no encontrada");
                return ResponseEntity.notFound().build();
            }

            TallaConfiguracion actualizada = existente.get();

            // Validar que no exista otra talla con el mismo nombre para el producto
            if (!actualizada.getTalla().equals(configuracion.getTalla()) &&
                    repository.existsByProductoIdAndTalla(actualizada.getProducto().getId(), configuracion.getTalla())) {
                response.put("success", false);
                response.put("message", "Ya existe esta talla para el producto");
                return ResponseEntity.badRequest().body(response);
            }

            actualizada.setTalla(configuracion.getTalla());
            actualizada.setPrecio(configuracion.getPrecio());
            actualizada.setStock(configuracion.getStock());

            TallaConfiguracion updated = repository.save(actualizada);
            productoService.actualizarStockProducto(updated.getProducto().getId());

            response.put("success", true);
            response.put("data", updated);
            return ResponseEntity.ok(response);

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
            Optional<TallaConfiguracion> configuracion = repository.findById(id);
            if (configuracion.isEmpty()) {
                response.put("success", false);
                response.put("message", "Configuración de talla no encontrada");
                return ResponseEntity.notFound().build();
            }

            Long productoId = configuracion.get().getProducto().getId();
            repository.deleteById(id);
            productoService.actualizarStockProducto(productoId);

            response.put("success", true);
            response.put("message", "Talla eliminada correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> actualizarStockTalla(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (cantidad < 0) {
                response.put("success", false);
                response.put("message", "El stock no puede ser negativo");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<TallaConfiguracion> tallaOpt = repository.findById(id);
            if (tallaOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Configuración de talla no encontrada");
                return ResponseEntity.notFound().build();
            }

            TallaConfiguracion talla = tallaOpt.get();
            talla.setStock(cantidad);
            repository.save(talla);
            productoService.actualizarStockProducto(talla.getProducto().getId());

            response.put("success", true);
            response.put("message", "Stock actualizado correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar stock: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}/reducir-stock")
    public ResponseEntity<Map<String, Object>> reducirStockTalla(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (cantidad <= 0) {
                response.put("success", false);
                response.put("message", "La cantidad debe ser mayor que cero");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<TallaConfiguracion> tallaOpt = repository.findById(id);
            if (tallaOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Configuración de talla no encontrada");
                return ResponseEntity.notFound().build();
            }

            TallaConfiguracion talla = tallaOpt.get();
            if (talla.getStock() < cantidad) {
                response.put("success", false);
                response.put("message", "Stock insuficiente");
                return ResponseEntity.badRequest().body(response);
            }

            boolean exito = productoService.reducirStock(talla.getProducto().getId(), talla.getTalla(), cantidad);
            if (!exito) {
                response.put("success", false);
                response.put("message", "No se pudo reducir el stock");
                return ResponseEntity.badRequest().body(response);
            }

            productoService.actualizarStockProducto(talla.getProducto().getId());

            response.put("success", true);
            response.put("message", "Stock reducido correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al reducir stock: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}/aumentar-stock")
    public ResponseEntity<Map<String, Object>> aumentarStockTalla(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (cantidad <= 0) {
                response.put("success", false);
                response.put("message", "La cantidad debe ser mayor que cero");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<TallaConfiguracion> tallaOpt = repository.findById(id);
            if (tallaOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Configuración de talla no encontrada");
                return ResponseEntity.notFound().build();
            }

            TallaConfiguracion talla = tallaOpt.get();
            boolean exito = productoService.aumentarStock(talla.getProducto().getId(), talla.getTalla(), cantidad);
            if (!exito) {
                response.put("success", false);
                response.put("message", "No se pudo aumentar el stock");
                return ResponseEntity.badRequest().body(response);
            }

            productoService.actualizarStockProducto(talla.getProducto().getId());

            response.put("success", true);
            response.put("message", "Stock aumentado correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al aumentar stock: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}