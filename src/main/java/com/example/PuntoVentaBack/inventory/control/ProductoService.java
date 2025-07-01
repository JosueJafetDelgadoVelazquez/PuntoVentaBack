package com.example.PuntoVentaBack.inventory.control;

import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracionRepository;
import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.inventory.model.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TallaConfiguracionRepository tallaConfiguracionRepository;

    @Transactional
    public int calcularStockTotal(Long productoId) {
        Integer stockTotal = tallaConfiguracionRepository.sumStockByProductoId(productoId);
        return stockTotal != null ? stockTotal : 0;
    }

    @Transactional
    public boolean reducirStock(Long productoId, String talla, int cantidad) {
        int affectedRows = tallaConfiguracionRepository.reducirStock(productoId, talla, cantidad);
        if (affectedRows > 0) {
            actualizarStockProducto(productoId);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean aumentarStock(Long productoId, String talla, int cantidad) {
        int affectedRows = tallaConfiguracionRepository.aumentarStock(productoId, talla, cantidad);
        if (affectedRows > 0) {
            actualizarStockProducto(productoId);
            return true;
        }
        return false;
    }

    @Transactional
    public void actualizarStockProducto(Long productoId) {
        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto != null) {
            int nuevoStock = calcularStockTotal(productoId);
            producto.setStock(nuevoStock);
            productoRepository.save(producto);
        }
    }
}