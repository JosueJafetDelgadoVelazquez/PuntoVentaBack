package com.example.PuntoVentaBack.pagos.control;

import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracionRepository;
import com.example.PuntoVentaBack.inventory.control.ProductoService;
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
    private TallaConfiguracionRepository tallaConfiguracionRepository;

    @Autowired
    private ProductoService productoService;

    @Transactional
    public Pago procesarPago(PagoRequest request) {
        // 1. Guardar registro del pago
        Pago pago = new Pago();
        pago.setMetodoPago(request.getMetodoPago());
        pago.setTotalPagado(request.getTotal());
        pago.setPermiteStockNegativo(request.isPermiteStockNegativo());
        pago = pagoRepository.save(pago);

        // 2. Actualizar stocks por talla
        actualizarStocksProductos(request.getProductos(), request.isPermiteStockNegativo());

        return pago;
    }

    @Transactional
    protected void actualizarStocksProductos(List<PagoRequest.ProductoPago> productos, boolean permiteStockNegativo) {
        for (PagoRequest.ProductoPago pp : productos) {
            // Buscar la configuración de talla específica
            TallaConfiguracion talla = tallaConfiguracionRepository
                    .findByProductoIdAndTalla(pp.getProductoId(), pp.getTalla())
                    .orElseThrow(() -> new RuntimeException("Talla no encontrada para el producto"));

            int nuevoStock = talla.getStock() - pp.getCantidad();

            // Validación condicional para stock negativo
            if (!permiteStockNegativo && nuevoStock < 0) {
                throw new RuntimeException("Stock insuficiente para la talla: " + pp.getTalla());
            }

            // Actualizar el stock de la talla
            talla.setStock(nuevoStock);
            tallaConfiguracionRepository.save(talla);

            // Actualizar el stock total del producto
            productoService.actualizarStockProducto(pp.getProductoId());
        }
    }
}