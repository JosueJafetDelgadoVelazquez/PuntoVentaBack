package com.example.PuntoVentaBack.inventory.control;

import com.example.PuntoVentaBack.TallasCategory.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasCategory.model.TallaConfiguracionRepository;
import com.example.PuntoVentaBack.category.model.TallasCategoria;
import com.example.PuntoVentaBack.category.model.TallasCategoriaRepository;
import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.inventory.model.ProductoRepository;
import com.example.PuntoVentaBack.inventory.dto.ProductoConTallasDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TallasCategoriaRepository tallasCategoriaRepository;

    @Autowired
    private TallaConfiguracionRepository tallaConfiguracionRepository;

    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody ProductoConTallasDTO dto) {
        try {
            // Validación básica
            if (dto.nombre == null || dto.nombre.isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre es obligatorio");
            }
            if (dto.codigoBarras == null || dto.codigoBarras.isEmpty()) {
                return ResponseEntity.badRequest().body("El código de barras es obligatorio");
            }

            Producto producto = new Producto();
            producto.setNombre(dto.nombre);
            producto.setCodigoBarras(dto.codigoBarras);
            producto.setStock(dto.stock);
            producto.setDescripcion(dto.descripcion);
            producto.setImagen(dto.imagen);
            producto.setCategoriaProducto(dto.categoriaProducto);
            producto.setSexo(dto.sexo);

            // Asignar categoría de tallas si existe
            if (dto.idTallasCategoria != null) {
                TallasCategoria tallasCategoria = tallasCategoriaRepository.findById(dto.idTallasCategoria)
                        .orElseThrow(() -> new RuntimeException("Categoría de tallas no encontrada"));
                producto.setTallasCategoria(tallasCategoria);
            }

            Producto productoGuardado = productoRepository.save(producto);

            // Guardar tallas si se proporcionaron
            if (dto.tallas != null && !dto.tallas.isEmpty()) {
                for (ProductoConTallasDTO.TallaPrecioDTO t : dto.tallas) {
                    TallaConfiguracion conf = new TallaConfiguracion();
                    conf.setProducto(productoGuardado);
                    conf.setTalla(t.talla);
                    conf.setPrecio(t.precio);

                    if (productoGuardado.getTallasCategoria() != null) {
                        conf.setTallaCategoria(productoGuardado.getTallasCategoria());
                    }

                    tallaConfiguracionRepository.save(conf);
                }
            }

            return ResponseEntity.ok(productoGuardado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear producto: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodosProductos() {
        try {
            List<Producto> productos = productoRepository.findAll();
            if (productos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener productos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
        try {
            Optional<Producto> producto = productoRepository.findById(id);
            if (producto.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(producto.get());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener producto: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductoConTallasDTO dto) {
        try {
            Optional<Producto> productoExistente = productoRepository.findById(id);
            if (productoExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Producto producto = productoExistente.get();
            producto.setNombre(dto.nombre);
            producto.setCodigoBarras(dto.codigoBarras);
            producto.setStock(dto.stock);
            producto.setDescripcion(dto.descripcion);
            producto.setImagen(dto.imagen);
            producto.setCategoriaProducto(dto.categoriaProducto);
            producto.setSexo(dto.sexo);

            // Actualizar categoría de tallas si se proporciona
            if (dto.idTallasCategoria != null) {
                TallasCategoria tallasCategoria = tallasCategoriaRepository.findById(dto.idTallasCategoria)
                        .orElseThrow(() -> new RuntimeException("Categoría de tallas no encontrada"));
                producto.setTallasCategoria(tallasCategoria);
            }

            Producto productoActualizado = productoRepository.save(producto);

            // Actualizar tallas (opcional, dependiendo de tu lógica de negocio)
            // Podrías eliminar las existentes y crear nuevas, o actualizar las que coincidan

            return ResponseEntity.ok(productoActualizado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            if (!productoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            // Primero eliminar las configuraciones de tallas asociadas
            List<TallaConfiguracion> tallas = tallaConfiguracionRepository.findByProductoId(id);
            tallaConfiguracionRepository.deleteAll(tallas);

            // Luego eliminar el producto
            productoRepository.deleteById(id);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar producto: " + e.getMessage());
        }
    }
}