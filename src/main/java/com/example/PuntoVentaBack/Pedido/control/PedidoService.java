package com.example.PuntoVentaBack.Pedido.control;

import com.example.PuntoVentaBack.Pedido.model.Pedido;
import com.example.PuntoVentaBack.Pedido.model.PedidoRepository;
import com.example.PuntoVentaBack.Pedido.dto.ProductoPedidoDTO;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracion;
import com.example.PuntoVentaBack.inventory.control.ProductoService;
import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.pagos.control.PagoService;
import com.example.PuntoVentaBack.pagos.model.Pago;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PagoService pagoService;
    private final ProductoService productoService;

    public PedidoService(PedidoRepository pedidoRepository,
                         PagoService pagoService,
                         ProductoService productoService) {
        this.pedidoRepository = pedidoRepository;
        this.pagoService = pagoService;
        this.productoService = productoService;
    }

    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosPorPago(Long pagoId) {
        return pedidoRepository.findByPagoId(pagoId);
    }

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosPorProducto(Long productoId) {
        return pedidoRepository.findByProductoId(productoId);
    }

    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        if (pedido.getId() != null && pedidoRepository.existsById(pedido.getId())) {
            throw new RuntimeException("El pedido ya existe con ID: " + pedido.getId());
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido actualizarPedido(Long id, Pedido pedido) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }
        pedido.setId(id);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void eliminarPedido(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existePedido(Long id) {
        return pedidoRepository.existsById(id);
    }

    @Transactional
    public List<Pedido> registrarPedidosMultiple(Long pagoId, List<ProductoPedidoDTO> productosDTO) {
        Pago pago = pagoService.obtenerPagoPorId(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + pagoId));

        List<Pedido> pedidos = new ArrayList<>();

        for (ProductoPedidoDTO productoDTO : productosDTO) {
            Producto producto = productoService.obtenerProductoPorId(productoDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoDTO.getProductoId()));

            // Buscar la talla correspondiente
            TallaConfiguracion tallaConfig = producto.getTallasConfiguracion().stream()
                    .filter(tc -> tc.getTalla().equalsIgnoreCase(productoDTO.getTalla()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Talla '" + productoDTO.getTalla() + "' no encontrada para el producto."));

            // Eliminada la validación de stock - siempre permite actualizar
            tallaConfig.setStock(tallaConfig.getStock() - productoDTO.getCantidad());

            // Guardar cambio de stock
            productoService.guardarTallaConfiguracion(tallaConfig);

            // Crear el pedido
            Pedido pedido = new Pedido();
            pedido.setNombreProducto(productoDTO.getNombreProducto());
            pedido.setCantidad(productoDTO.getCantidad());
            pedido.setPagoProducto(productoDTO.getPrecio());
            pedido.setTalla(productoDTO.getTalla());
            pedido.setPago(pago);
            pedido.setProducto(producto);

            pedidos.add(pedidoRepository.save(pedido));
        }

        return pedidos;
    }
}