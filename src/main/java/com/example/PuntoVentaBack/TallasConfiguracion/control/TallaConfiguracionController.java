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
    public ResponseEntity<?> crearTallaConfiguracion(@RequestBody TallaConfiguracion configuracion) {
        try {
            // Validaciones
            if (configuracion.getTalla() == null || configuracion.getTalla().isEmpty()) {
                return ResponseEntity.badRequest().body("La talla es obligatoria");
            }
            if (configuracion.getPrecio() <= 0) {
                return ResponseEntity.badRequest().body("El precio debe ser mayor que cero");
            }
            if (configuracion.getProducto() == null || configuracion.getProducto().getId() == null) {
                return ResponseEntity.badRequest().body("El producto es obligatorio");
            }
            if (configuracion.getStock() < 0) {
                return ResponseEntity.badRequest().body("El stock no puede ser negativo");
            }

            TallaConfiguracion nuevaConfiguracion = repository.save(configuracion);
            productoService.actualizarStockProducto(configuracion.getProducto().getId());

            return ResponseEntity.ok(nuevaConfiguracion);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar la configuración: " + e.getMessage());
        }
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> obtenerTallasPorProducto(@PathVariable Long productoId) {
        try {
            List<TallaConfiguracion> tallas = repository.findByProductoId(productoId);

            if (tallas == null || tallas.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            // Convertir a lista de mapas para una respuesta más limpia
            List<Map<String, Object>> response = tallas.stream()
                    .map(t -> {
                        Map<String, Object> tallaMap = new HashMap<>();
                        tallaMap.put("id", t.getId());
                        tallaMap.put("talla", t.getTalla());
                        tallaMap.put("precio", t.getPrecio());
                        tallaMap.put("stock", t.getStock());
                        return tallaMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al obtener tallas: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTallaConfiguracion(
            @PathVariable Long id,
            @RequestBody TallaConfiguracion configuracion) {
        try {
            Optional<TallaConfiguracion> existente = repository.findById(id);
            if (existente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TallaConfiguracion actualizada = existente.get();
            actualizada.setTalla(configuracion.getTalla());
            actualizada.setPrecio(configuracion.getPrecio());
            actualizada.setStock(configuracion.getStock());

            TallaConfiguracion updated = repository.save(actualizada);
            productoService.actualizarStockProducto(updated.getProducto().getId());

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTallaConfiguracion(@PathVariable Long id) {
        try {
            Optional<TallaConfiguracion> configuracion = repository.findById(id);
            if (configuracion.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Long productoId = configuracion.get().getProducto().getId();
            repository.deleteById(id);
            productoService.actualizarStockProducto(productoId);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStockTalla(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        try {
            if (cantidad < 0) {
                return ResponseEntity.badRequest().body("El stock no puede ser negativo");
            }

            Optional<TallaConfiguracion> tallaOpt = repository.findById(id);
            if (tallaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TallaConfiguracion talla = tallaOpt.get();
            talla.setStock(cantidad);
            repository.save(talla);
            productoService.actualizarStockProducto(talla.getProducto().getId());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar stock: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reducir-stock")
    public ResponseEntity<?> reducirStockTalla(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        try {
            if (cantidad <= 0) {
                return ResponseEntity.badRequest().body("La cantidad debe ser mayor que cero");
            }

            Optional<TallaConfiguracion> tallaOpt = repository.findById(id);
            if (tallaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TallaConfiguracion talla = tallaOpt.get();
            if (talla.getStock() < cantidad) {
                return ResponseEntity.badRequest().body("Stock insuficiente");
            }

            boolean exito = productoService.reducirStock(talla.getProducto().getId(), talla.getTalla(), cantidad);
            if (!exito) {
                return ResponseEntity.badRequest().body("No se pudo reducir el stock");
            }

            productoService.actualizarStockProducto(talla.getProducto().getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al reducir stock: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/aumentar-stock")
    public ResponseEntity<?> aumentarStockTalla(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        try {
            if (cantidad <= 0) {
                return ResponseEntity.badRequest().body("La cantidad debe ser mayor que cero");
            }

            Optional<TallaConfiguracion> tallaOpt = repository.findById(id);
            if (tallaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TallaConfiguracion talla = tallaOpt.get();
            boolean exito = productoService.aumentarStock(talla.getProducto().getId(), talla.getTalla(), cantidad);
            if (!exito) {
                return ResponseEntity.badRequest().body("No se pudo aumentar el stock");
            }

            productoService.actualizarStockProducto(talla.getProducto().getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al aumentar stock: " + e.getMessage());
        }
    }
}