package com.example.PuntoVentaBack.inventory.control;

import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracionRepository;
import com.example.PuntoVentaBack.category.model.TallasCategoria;
import com.example.PuntoVentaBack.category.model.TallasCategoriaRepository;
import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.inventory.model.ProductoRepository;
import com.example.PuntoVentaBack.inventory.dto.ProductoConTallasDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TallasCategoriaRepository tallasCategoriaRepository;

    @Autowired
    private TallaConfiguracionRepository tallaConfiguracionRepository;

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosProductos() {
        try {
            List<Producto> productos = productoRepository.findAll();
            return ResponseEntity.ok(productos != null ? productos : Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/tallas-configuracion/producto/{id}")
    public ResponseEntity<List<TallaConfiguracion>> obtenerTallasPorProducto(@PathVariable Long id) {
        try {
            List<TallaConfiguracion> tallas = tallaConfiguracionRepository.findByProductoId(id);
            return ResponseEntity.ok(tallas != null ? tallas : Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
        try {
            Optional<Producto> producto = productoRepository.findById(id);
            return producto.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.ok().body(null));
        } catch (Exception e) {
            return ResponseEntity.ok().body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody ProductoConTallasDTO dto) {
        try {
            if (dto.nombre == null || dto.nombre.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "El nombre es obligatorio"));
            }
            if (dto.codigoBarras == null || dto.codigoBarras.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "El código de barras es obligatorio"));
            }

            Producto producto = new Producto();
            producto.setNombre(dto.nombre);
            producto.setCodigoBarras(dto.codigoBarras);
            producto.setStock(dto.stock);
            producto.setDescripcion(dto.descripcion);
            producto.setImagen(dto.imagen);
            producto.setCategoriaProducto(dto.categoriaProducto);
            producto.setSexo(dto.sexo);
            producto.setHabilitado(true);

            if (dto.idTallasCategoria != null) {
                TallasCategoria tallasCategoria = tallasCategoriaRepository.findById(dto.idTallasCategoria)
                        .orElseThrow(() -> new RuntimeException("Categoría de tallas no encontrada"));
                producto.setTallasCategoria(tallasCategoria);
            }

            Producto productoGuardado = productoRepository.save(producto);

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
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "Error al crear producto: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody ProductoConTallasDTO dto) {
        try {
            if (dto.nombre == null || dto.nombre.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "El nombre es obligatorio"));
            }
            if (dto.codigoBarras == null || dto.codigoBarras.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "El código de barras es obligatorio"));
            }

            Optional<Producto> productoExistente = productoRepository.findById(id);
            if (productoExistente.isEmpty()) {
                return ResponseEntity.ok().body(null);
            }

            Producto producto = productoExistente.get();
            producto.setNombre(dto.nombre);
            producto.setCodigoBarras(dto.codigoBarras);
            producto.setStock(dto.stock);
            producto.setDescripcion(dto.descripcion);
            producto.setImagen(dto.imagen);
            producto.setCategoriaProducto(dto.categoriaProducto);
            producto.setSexo(dto.sexo);

            if (dto.idTallasCategoria != null) {
                TallasCategoria tallasCategoria = tallasCategoriaRepository.findById(dto.idTallasCategoria)
                        .orElseThrow(() -> new RuntimeException("Categoría de tallas no encontrada"));
                producto.setTallasCategoria(tallasCategoria);
            } else {
                producto.setTallasCategoria(null);
            }

            Producto productoActualizado = productoRepository.save(producto);

            List<TallaConfiguracion> tallasExistentes = tallaConfiguracionRepository.findByProductoId(id);
            tallaConfiguracionRepository.deleteAll(tallasExistentes);

            if (dto.tallas != null && !dto.tallas.isEmpty()) {
                for (ProductoConTallasDTO.TallaPrecioDTO t : dto.tallas) {
                    TallaConfiguracion conf = new TallaConfiguracion();
                    conf.setProducto(productoActualizado);
                    conf.setTalla(t.talla);
                    conf.setPrecio(t.precio);

                    if (productoActualizado.getTallasCategoria() != null) {
                        conf.setTallaCategoria(productoActualizado.getTallasCategoria());
                    }

                    tallaConfiguracionRepository.save(conf);
                }
            }

            return ResponseEntity.ok(productoActualizado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "Error al actualizar producto: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            if (!productoRepository.existsById(id)) {
                return ResponseEntity.ok().body(null);
            }

            List<TallaConfiguracion> tallas = tallaConfiguracionRepository.findByProductoId(id);
            tallaConfiguracionRepository.deleteAll(tallas);

            productoRepository.deleteById(id);

            return ResponseEntity.ok().body(Collections.singletonMap("message", "Producto eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "Error al eliminar producto: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/habilitar")
    public ResponseEntity<?> habilitarProducto(@PathVariable Long id) {
        try {
            Optional<Producto> optionalProducto = productoRepository.findById(id);
            if (optionalProducto.isEmpty()) {
                return ResponseEntity.ok().body(null);
            }
            Producto producto = optionalProducto.get();
            producto.setHabilitado(true);
            productoRepository.save(producto);
            return ResponseEntity.ok(Collections.singletonMap("message", "Producto habilitado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "Error al habilitar producto: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/deshabilitar")
    public ResponseEntity<?> deshabilitarProducto(@PathVariable Long id) {
        try {
            Optional<Producto> optionalProducto = productoRepository.findById(id);
            if (optionalProducto.isEmpty()) {
                return ResponseEntity.ok().body(null);
            }
            Producto producto = optionalProducto.get();
            producto.setHabilitado(false);
            productoRepository.save(producto);
            return ResponseEntity.ok(Collections.singletonMap("message", "Producto deshabilitado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "Error al deshabilitar producto: " + e.getMessage()));
        }
    }
}