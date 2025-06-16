package com.example.PuntoVentaBack.TallasCategory.control;

import com.example.PuntoVentaBack.TallasCategory.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasCategory.model.TallaConfiguracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tallas-configuracion")
@CrossOrigin(origins = "*")
public class TallaConfiguracionController {

    @Autowired
    private TallaConfiguracionRepository repository;

    @PostMapping
    public ResponseEntity<?> crearTallaConfiguracion(@RequestBody TallaConfiguracion configuracion) {
        try {
            // Validación básica
            if (configuracion.getTalla() == null || configuracion.getTalla().isEmpty()) {
                return ResponseEntity.badRequest().body("La talla es obligatoria");
            }
            if (configuracion.getPrecio() <= 0) {
                return ResponseEntity.badRequest().body("El precio debe ser mayor que cero");
            }
            if (configuracion.getProducto() == null || configuracion.getProducto().getId() == null) {
                return ResponseEntity.badRequest().body("El producto es obligatorio");
            }

            TallaConfiguracion nuevaConfiguracion = repository.save(configuracion);
            return ResponseEntity.ok(nuevaConfiguracion);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar la configuración: " + e.getMessage());
        }
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> obtenerTallasPorProducto(@PathVariable Long productoId) {
        try {
            List<TallaConfiguracion> tallas = repository.findByProductoId(productoId);
            if (tallas.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(tallas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener tallas: " + e.getMessage());
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

            return ResponseEntity.ok(repository.save(actualizada));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTallaConfiguracion(@PathVariable Long id) {
        try {
            if (!repository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar: " + e.getMessage());
        }
    }
}