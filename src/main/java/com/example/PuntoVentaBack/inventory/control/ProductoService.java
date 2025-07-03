package com.example.PuntoVentaBack.inventory.control;

import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracionRepository;
import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.inventory.model.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TallaConfiguracionRepository tallaConfiguracionRepository;

    @Transactional(readOnly = true)
    public boolean existeProducto(Long productoId) {
        return productoRepository.existsById(productoId);
    }

    @Transactional(readOnly = true)
    public Optional<Producto> obtenerProductoPorId(Long productoId) {
        return productoRepository.findById(productoId);
    }

    @Transactional(readOnly = true)
    public int calcularStockTotal(Long productoId) {
        Integer stockTotal = tallaConfiguracionRepository.sumStockByProductoId(productoId);
        return stockTotal != null ? stockTotal : 0;
    }

    @Transactional
    public boolean reducirStock(Long productoId, String talla, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Optional<TallaConfiguracion> tallaOpt = tallaConfiguracionRepository
                .findByProductoIdAndTalla(productoId, talla);

        if (tallaOpt.isEmpty()) {
            return false;
        }

        TallaConfiguracion tallaConfig = tallaOpt.get();
        if (tallaConfig.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente");
        }

        tallaConfig.setStock(tallaConfig.getStock() - cantidad);
        tallaConfiguracionRepository.save(tallaConfig);
        actualizarStockProducto(productoId);
        return true;
    }

    @Transactional
    public boolean aumentarStock(Long productoId, String talla, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Optional<TallaConfiguracion> tallaOpt = tallaConfiguracionRepository
                .findByProductoIdAndTalla(productoId, talla);

        if (tallaOpt.isEmpty()) {
            return false;
        }

        TallaConfiguracion tallaConfig = tallaOpt.get();
        tallaConfig.setStock(tallaConfig.getStock() + cantidad);
        tallaConfiguracionRepository.save(tallaConfig);
        actualizarStockProducto(productoId);
        return true;
    }

    @Transactional
    public void actualizarStockProducto(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        int nuevoStock = calcularStockTotal(productoId);
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
    }

    @Transactional
    public void actualizarTallasProducto(Long productoId, List<TallaConfiguracion> tallas) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // Validar que todas las tallas pertenezcan al producto
        tallas.forEach(t -> {
            if (!t.getProducto().getId().equals(productoId)) {
                throw new IllegalArgumentException("Talla no pertenece al producto");
            }
        });

        // Actualizar el stock del producto
        actualizarStockProducto(productoId);
    }

    @Transactional
    public void eliminarTallasDeProducto(Long productoId, List<String> tallas) {
        if (tallas == null || tallas.isEmpty()) {
            return;
        }

        List<TallaConfiguracion> tallasAEliminar = tallaConfiguracionRepository
                .findByProductoIdAndTallaIn(productoId, tallas);

        if (!tallasAEliminar.isEmpty()) {
            tallaConfiguracionRepository.deleteAll(tallasAEliminar);
            actualizarStockProducto(productoId);
        }
    }
}