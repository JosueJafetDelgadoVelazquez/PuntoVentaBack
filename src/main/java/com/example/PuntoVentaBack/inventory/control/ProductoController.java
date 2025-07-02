package com.example.PuntoVentaBack.inventory.control;

import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracionRepository;
import com.example.PuntoVentaBack.category.model.TallasCategoria;
import com.example.PuntoVentaBack.category.model.TallasCategoriaRepository;
import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.inventory.model.ProductoRepository;
import com.example.PuntoVentaBack.inventory.dto.ProductoConTallasDTO;
import com.example.PuntoVentaBack.inventory.dto.ProductoResponseDTO;
import com.example.PuntoVentaBack.inventory.dto.TallaConfiguracionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TallasCategoriaRepository tallasCategoriaRepository;

    @Autowired
    private TallaConfiguracionRepository tallaConfiguracionRepository;

    @GetMapping("/tallas-categoria")
    public ResponseEntity<List<TallasCategoria>> obtenerTodasTallasCategoria() {
        try {
            List<TallasCategoria> categorias = tallasCategoriaRepository.findAll();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodosProductos() {
        try {
            List<Producto> productos = productoRepository.findAll();

            List<ProductoResponseDTO> response = productos.stream().map(producto -> {
                ProductoResponseDTO dto = new ProductoResponseDTO();
                dto.setId(producto.getId());
                dto.setNombre(producto.getNombre());
                dto.setCodigoBarras(producto.getCodigoBarras());
                dto.setDescripcion(producto.getDescripcion());
                dto.setImagen(producto.getImagen());
                dto.setCategoriaProducto(producto.getCategoriaProducto());
                dto.setSexo(producto.getSexo());
                dto.setHabilitado(producto.isHabilitado());

                if(producto.getTallasCategoria() != null) {
                    dto.setIdTallasCategoria(producto.getTallasCategoria().getId());
                    dto.setNombreTallasCategoria(producto.getTallasCategoria().getNombre());
                }

                // Fetch sizes directly from repository to ensure we get all sizes
                List<TallaConfiguracion> tallasConfig = tallaConfiguracionRepository.findByProductoId(producto.getId());

                if(tallasConfig != null && !tallasConfig.isEmpty()) {
                    List<TallaConfiguracionDTO> tallasDTO = tallasConfig.stream()
                            .map(t -> {
                                TallaConfiguracionDTO tallaDTO = new TallaConfiguracionDTO();
                                tallaDTO.setId(t.getId());
                                tallaDTO.setTalla(t.getTalla());
                                tallaDTO.setPrecio(t.getPrecio());
                                tallaDTO.setStock(t.getStock());
                                return tallaDTO;
                            })
                            .collect(Collectors.toList());
                    dto.setTallas(tallasDTO);
                    dto.setStock(tallasDTO.stream().mapToInt(TallaConfiguracionDTO::getStock).sum());
                } else {
                    dto.setStock(0);
                }

                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

    @GetMapping("/tallas-configuracion/producto/{id}")
    public ResponseEntity<?> obtenerTallasPorProducto(@PathVariable Long id) {
        try {
            List<TallaConfiguracion> tallas = tallaConfiguracionRepository.findByProductoId(id);

            if (tallas == null || tallas.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<TallaConfiguracionDTO> response = tallas.stream()
                    .map(t -> {
                        TallaConfiguracionDTO dto = new TallaConfiguracionDTO();
                        dto.setId(t.getId());
                        dto.setTalla(t.getTalla());
                        dto.setPrecio(t.getPrecio());
                        dto.setStock(t.getStock());
                        return dto;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al obtener tallas: " + e.getMessage());
        }
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
        try {
            Optional<Producto> producto = productoRepository.findById(id);
            if (producto.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Producto p = producto.get();
            ProductoResponseDTO response = new ProductoResponseDTO();
            response.setId(p.getId());
            response.setNombre(p.getNombre());
            response.setCodigoBarras(p.getCodigoBarras());
            response.setDescripcion(p.getDescripcion());
            response.setImagen(p.getImagen());
            response.setCategoriaProducto(p.getCategoriaProducto());
            response.setSexo(p.getSexo());
            response.setHabilitado(p.isHabilitado());

            if(p.getTallasCategoria() != null) {
                response.setIdTallasCategoria(p.getTallasCategoria().getId());
                response.setNombreTallasCategoria(p.getTallasCategoria().getNombre());
            }

            // Fetch sizes directly from repository
            List<TallaConfiguracion> tallasConfig = tallaConfiguracionRepository.findByProductoId(p.getId());

            if(tallasConfig != null && !tallasConfig.isEmpty()) {
                List<TallaConfiguracionDTO> tallasDTO = tallasConfig.stream()
                        .map(t -> {
                            TallaConfiguracionDTO dto = new TallaConfiguracionDTO();
                            dto.setId(t.getId());
                            dto.setTalla(t.getTalla());
                            dto.setPrecio(t.getPrecio());
                            dto.setStock(t.getStock());
                            return dto;
                        })
                        .collect(Collectors.toList());
                response.setTallas(tallasDTO);
                response.setStock(tallasDTO.stream().mapToInt(TallaConfiguracionDTO::getStock).sum());
            } else {
                response.setStock(0);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al obtener producto: " + e.getMessage());
        }
    }

    @PostMapping("/productos")
    public ResponseEntity<?> crearProducto(@RequestBody ProductoConTallasDTO dto) {
        try {
            if (dto.getNombre() == null || dto.getNombre().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre es obligatorio");
            }
            if (dto.getCodigoBarras() == null || dto.getCodigoBarras().isEmpty()) {
                return ResponseEntity.badRequest().body("El código de barras es obligatorio");
            }

            Producto producto = new Producto();
            producto.setNombre(dto.getNombre());
            producto.setCodigoBarras(dto.getCodigoBarras());
            producto.setDescripcion(dto.getDescripcion());
            producto.setImagen(dto.getImagen());
            producto.setCategoriaProducto(dto.getCategoriaProducto());
            producto.setSexo(dto.getSexo());
            producto.setHabilitado(true);

            if (dto.getIdTallasCategoria() != null) {
                TallasCategoria tallasCategoria = tallasCategoriaRepository.findById(dto.getIdTallasCategoria())
                        .orElseThrow(() -> new RuntimeException("Categoría de tallas no encontrada"));
                producto.setTallasCategoria(tallasCategoria);
            }

            Producto productoGuardado = productoRepository.save(producto);

            if (dto.getTallas() != null && !dto.getTallas().isEmpty()) {
                for (ProductoConTallasDTO.TallaPrecioStockDTO t : dto.getTallas()) {
                    TallaConfiguracion conf = new TallaConfiguracion();
                    conf.setProducto(productoGuardado);
                    conf.setTalla(t.getTalla());
                    conf.setPrecio(t.getPrecio());
                    conf.setStock(t.getStock());

                    if (productoGuardado.getTallasCategoria() != null) {
                        conf.setTallaCategoria(productoGuardado.getTallasCategoria());
                    }

                    tallaConfiguracionRepository.save(conf);
                }
            }

            return ResponseEntity.ok(convertToResponseDTO(productoGuardado));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al crear producto: " + e.getMessage());
        }
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody ProductoConTallasDTO dto) {
        try {
            if (dto.getNombre() == null || dto.getNombre().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre es obligatorio");
            }
            if (dto.getCodigoBarras() == null || dto.getCodigoBarras().isEmpty()) {
                return ResponseEntity.badRequest().body("El código de barras es obligatorio");
            }

            Optional<Producto> productoExistente = productoRepository.findById(id);
            if (productoExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Producto producto = productoExistente.get();
            producto.setNombre(dto.getNombre());
            producto.setCodigoBarras(dto.getCodigoBarras());
            producto.setDescripcion(dto.getDescripcion());
            producto.setImagen(dto.getImagen());
            producto.setCategoriaProducto(dto.getCategoriaProducto());
            producto.setSexo(dto.getSexo());

            if (dto.getIdTallasCategoria() != null) {
                TallasCategoria tallasCategoria = tallasCategoriaRepository.findById(dto.getIdTallasCategoria())
                        .orElseThrow(() -> new RuntimeException("Categoría de tallas no encontrada"));
                producto.setTallasCategoria(tallasCategoria);
            } else {
                producto.setTallasCategoria(null);
            }

            Producto productoActualizado = productoRepository.save(producto);

            // Eliminar tallas existentes y crear nuevas
            tallaConfiguracionRepository.deleteByProductoId(id);

            if (dto.getTallas() != null && !dto.getTallas().isEmpty()) {
                for (ProductoConTallasDTO.TallaPrecioStockDTO t : dto.getTallas()) {
                    TallaConfiguracion conf = new TallaConfiguracion();
                    conf.setProducto(productoActualizado);
                    conf.setTalla(t.getTalla());
                    conf.setPrecio(t.getPrecio());
                    conf.setStock(t.getStock());

                    if (productoActualizado.getTallasCategoria() != null) {
                        conf.setTallaCategoria(productoActualizado.getTallasCategoria());
                    }

                    tallaConfiguracionRepository.save(conf);
                }
            }

            return ResponseEntity.ok(convertToResponseDTO(productoActualizado));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al actualizar producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            if (!productoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            // Eliminar primero las tallas asociadas
            tallaConfiguracionRepository.deleteByProductoId(id);

            // Luego eliminar el producto
            productoRepository.deleteById(id);

            return ResponseEntity.ok().body("Producto eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al eliminar producto: " + e.getMessage());
        }
    }

    @PatchMapping("/productos/{id}/habilitar")
    public ResponseEntity<?> habilitarProducto(@PathVariable Long id) {
        try {
            Optional<Producto> optionalProducto = productoRepository.findById(id);
            if (optionalProducto.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Producto producto = optionalProducto.get();
            producto.setHabilitado(true);
            productoRepository.save(producto);
            return ResponseEntity.ok("Producto habilitado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al habilitar producto: " + e.getMessage());
        }
    }

    @PatchMapping("/productos/{id}/deshabilitar")
    public ResponseEntity<?> deshabilitarProducto(@PathVariable Long id) {
        try {
            Optional<Producto> optionalProducto = productoRepository.findById(id);
            if (optionalProducto.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Producto producto = optionalProducto.get();
            producto.setHabilitado(false);
            productoRepository.save(producto);
            return ResponseEntity.ok("Producto deshabilitado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al deshabilitar producto: " + e.getMessage());
        }
    }

    private ProductoResponseDTO convertToResponseDTO(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setCodigoBarras(producto.getCodigoBarras());
        dto.setDescripcion(producto.getDescripcion());
        dto.setImagen(producto.getImagen());
        dto.setCategoriaProducto(producto.getCategoriaProducto());
        dto.setSexo(producto.getSexo());
        dto.setHabilitado(producto.isHabilitado());

        if(producto.getTallasCategoria() != null) {
            dto.setIdTallasCategoria(producto.getTallasCategoria().getId());
            dto.setNombreTallasCategoria(producto.getTallasCategoria().getNombre());
        }

        // Fetch sizes directly from repository
        List<TallaConfiguracion> tallasConfig = tallaConfiguracionRepository.findByProductoId(producto.getId());

        if(tallasConfig != null && !tallasConfig.isEmpty()) {
            List<TallaConfiguracionDTO> tallasDTO = tallasConfig.stream()
                    .map(t -> {
                        TallaConfiguracionDTO tallaDTO = new TallaConfiguracionDTO();
                        tallaDTO.setId(t.getId());
                        tallaDTO.setTalla(t.getTalla());
                        tallaDTO.setPrecio(t.getPrecio());
                        tallaDTO.setStock(t.getStock());
                        return tallaDTO;
                    })
                    .collect(Collectors.toList());
            dto.setTallas(tallasDTO);
            dto.setStock(tallasDTO.stream().mapToInt(TallaConfiguracionDTO::getStock).sum());
        } else {
            dto.setStock(0);
        }

        return dto;
    }
}