package com.example.PuntoVentaBack.pagos.control;

import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.inventory.model.ProductoRepository;
import com.example.PuntoVentaBack.pagos.model.Pago;
import com.example.PuntoVentaBack.pagos.model.PagoRepository;
import com.example.PuntoVentaBack.pagos.dto.PagoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public Pago procesarPago(PagoRequest request) {
        // 1. Guardar registro del pago
        Pago pago = new Pago();
        pago.setMetodoPago(request.getMetodoPago());
        pago.setTotalPagado(request.getTotal()); // Cambiado de setTotal a setTotalPagado
        pago.setPermiteStockNegativo(request.isPermiteStockNegativo());
        pago = pagoRepository.save(pago);

        // 2. Actualizar stocks
        actualizarStocksProductos(request.getProductos(), request.isPermiteStockNegativo());

        return pago;
    }

    @Transactional
    protected void actualizarStocksProductos(List<PagoRequest.ProductoPago> productos, boolean permiteStockNegativo) {
        for (PagoRequest.ProductoPago pp : productos) {
            Producto producto = productoRepository.findById(pp.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            int nuevoStock = producto.getStock() - pp.getCantidad();

            // Validación condicional para stock negativo
            if (!permiteStockNegativo && nuevoStock < 0) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            producto.setStock(nuevoStock);
            productoRepository.save(producto);
        }
    }
}