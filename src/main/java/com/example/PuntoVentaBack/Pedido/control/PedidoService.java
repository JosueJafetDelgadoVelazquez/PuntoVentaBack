package com.example.PuntoVentaBack.Pedido.control;

import com.example.PuntoVentaBack.Pedido.dto.VentasPorProductoDTO;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

    public List<Pedido> obtenerPedidosPorPago(Long pagoId) {
        return pedidoRepository.findByPagoId(pagoId);
    }

    public List<Pedido> obtenerPedidosPorProducto(Long productoId) {
        return pedidoRepository.findByProductoId(productoId);
    }

    public Pedido crearPedido(Pedido pedido) {
        if (pedido.getId() != null && pedidoRepository.existsById(pedido.getId())) {
            throw new RuntimeException("El pedido ya existe con ID: " + pedido.getId());
        }
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarPedido(Long id, Pedido pedido) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }
        pedido.setId(id);
        return pedidoRepository.save(pedido);
    }

    public void eliminarPedido(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    public boolean existePedido(Long id) {
        return pedidoRepository.existsById(id);
    }

    public List<Pedido> registrarPedidosMultiple(Long pagoId, List<ProductoPedidoDTO> productosDTO) {
        Pago pago = pagoService.obtenerPagoPorId(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + pagoId));

        List<Pedido> pedidos = new ArrayList<>();

        for (ProductoPedidoDTO productoDTO : productosDTO) {
            Producto producto = productoService.obtenerProductoPorId(productoDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoDTO.getProductoId()));

            TallaConfiguracion tallaConfig = producto.getTallasConfiguracion().stream()
                    .filter(tc -> tc.getTalla().equalsIgnoreCase(productoDTO.getTalla()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Talla '" + productoDTO.getTalla() + "' no encontrada para el producto."));

            tallaConfig.setStock(tallaConfig.getStock() - productoDTO.getCantidad());
            productoService.guardarTallaConfiguracion(tallaConfig);

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

    @Transactional(readOnly = true)
    public List<VentasPorProductoDTO> obtenerVentasPorProducto(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Object[]> results = pedidoRepository.findVentasAgrupadasPorProducto(fechaInicio, fechaFin);
        List<VentasPorProductoDTO> ventas = new ArrayList<>();

        for (Object[] result : results) {
            VentasPorProductoDTO dto = new VentasPorProductoDTO(
                    (Long) result[0],         // productoId
                    (String) result[1],       // nombreProducto
                    (String) result[2],       // talla
                    ((Number) result[3]).intValue(),  // cantidadVendida
                    (BigDecimal) result[4],   // totalVendido
                    (LocalDateTime) result[5] // fecha
            );
            ventas.add(dto);
        }

        return ventas;
    }
}