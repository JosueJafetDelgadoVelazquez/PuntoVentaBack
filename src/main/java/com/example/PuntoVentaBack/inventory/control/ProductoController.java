package com.example.PuntoVentaBack.inventory.control;

import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.inventory.model.ProductoRepository;
import com.example.PuntoVentaBack.category.model.TallasCategoria;
import com.example.PuntoVentaBack.category.model.TallasCategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TallasCategoriaRepository tallasCategoriaRepository;

    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto productoRequest) {
        try {
            // Crear nueva instancia de Producto
            Producto producto = new Producto();
            producto.setNombre(productoRequest.getNombre());
            producto.setCodigoBarras(productoRequest.getCodigoBarras());
            producto.setStock(productoRequest.getStock());
            producto.setDescripcion(productoRequest.getDescripcion());
            producto.setImagen(productoRequest.getImagen());
            producto.setCategoriaProducto(productoRequest.getCategoriaProducto());
            producto.setSexo(productoRequest.getSexo());
            // Manejo de la categoría de tallas
            if (productoRequest.getTallasCategoria() != null && productoRequest.getTallasCategoria().getId() != null) {
                TallasCategoria categoria = tallasCategoriaRepository.findById(productoRequest.getTallasCategoria().getId())
                        .orElseThrow(() -> new RuntimeException("Categoría de talla no encontrada"));
                producto.setTallasCategoria(categoria);
            }

            // Validaciones
            if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre es obligatorio");
            }

            Producto productoGuardado = productoRepository.save(producto);
            return ResponseEntity.ok(productoGuardado);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear producto: " + e.getMessage());
        }
    }
}