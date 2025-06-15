package com.example.PuntoVentaBack.inventory.control;

import com.example.PuntoVentaBack.TallasCategory.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasCategory.model.TallaConfiguracionRepository;
import com.example.PuntoVentaBack.category.model.TallasCategoria;
import com.example.PuntoVentaBack.category.model.TallasCategoriaRepository;
import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.inventory.model.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.PuntoVentaBack.inventory.dto.ProductoConTallasDTO;

import java.util.List;

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
            Producto producto = new Producto();
            producto.setNombre(dto.nombre);
            producto.setCodigoBarras(dto.codigoBarras);
            producto.setStock(dto.stock);
            producto.setDescripcion(dto.descripcion);
            producto.setImagen(dto.imagen);
            producto.setCategoriaProducto(dto.categoriaProducto);
            producto.setSexo(dto.sexo);

            if (dto.idTallasCategoria != null) {
                TallasCategoria categoria = tallasCategoriaRepository.findById(dto.idTallasCategoria)
                        .orElseThrow(() -> new RuntimeException("Categoría de talla no encontrada"));
                producto.setTallasCategoria(categoria);
            }

            // Validaciones
            if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre es obligatorio");
            }

            // Guardar producto
            Producto productoGuardado = productoRepository.save(producto);

            // Guardar tallas si se proporcionaron
            if (dto.tallas != null) {
                for (ProductoConTallasDTO.TallaPrecioDTO t : dto.tallas) {
                    TallaConfiguracion conf = new TallaConfiguracion();
                    conf.setProducto(productoGuardado);
                    conf.setTalla(t.talla);
                    conf.setPrecio(t.precio);
                    conf.setTallaCategoria(productoGuardado.getTallasCategoria());
                    tallaConfiguracionRepository.save(conf);
                }
            }

            return ResponseEntity.ok(productoGuardado);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear producto: " + e.getMessage());
        }
    }
}
