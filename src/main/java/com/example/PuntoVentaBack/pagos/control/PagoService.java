package com.example.PuntoVentaBack.pagos.control;

import com.example.PuntoVentaBack.Pedido.model.Pedido;
import com.example.PuntoVentaBack.Pedido.model.PedidoRepository;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracionRepository;
import com.example.PuntoVentaBack.inventory.control.ProductoService;
import com.example.PuntoVentaBack.pagos.model.Pago;
import com.example.PuntoVentaBack.pagos.model.PagoRepository;
import com.example.PuntoVentaBack.pagos.dto.PagoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final TallaConfiguracionRepository tallaConfiguracionRepository;
    private final ProductoService productoService;

    @Autowired
    public PagoService(PagoRepository pagoRepository,
                       TallaConfiguracionRepository tallaConfiguracionRepository,
                       ProductoService productoService,
                       PedidoRepository pedidoRepository) {
        this.pagoRepository = pagoRepository;
        this.tallaConfiguracionRepository = tallaConfiguracionRepository;
        this.productoService = productoService;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public Pago procesarPago(PagoRequest request) {
        validarPagoRequest(request);
        request.setPermiteStockNegativo(true);

        Pago pago = crearPago(request);
        List<Pedido> pedidos = crearPedidos(request, pago);

        pedidoRepository.saveAll(pedidos);
        actualizarStocks(request.getProductos());

        return pago;
    }

    private void validarPagoRequest(PagoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de pago no puede ser nula");
        }
        if (request.getProductos() == null || request.getProductos().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un producto en el pago");
        }
        if (request.getTotal() == null || request.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El total del pago debe ser mayor a cero");
        }
    }

    private Pago crearPago(PagoRequest request) {
        Pago pago = new Pago();
        pago.setMetodoPago(request.getMetodoPago());
        pago.setTotalPagado(request.getTotal());
        pago.setPermiteStockNegativo(true);
        pago.setFechaHora(LocalDateTime.now());
        return pagoRepository.save(pago);
    }

    private List<Pedido> crearPedidos(PagoRequest request, Pago pago) {
        return request.getProductos().stream()
                .map(producto -> {
                    TallaConfiguracion talla = obtenerTallaProducto(producto);
                    return crearPedido(producto, pago, talla);
                })
                .collect(Collectors.toList());
    }

    private TallaConfiguracion obtenerTallaProducto(PagoRequest.ProductoPago producto) {
        return tallaConfiguracionRepository
                .findByProductoIdAndTalla(producto.getProductoId(), producto.getTalla())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Talla no encontrada para el producto ID: " + producto.getProductoId() +
                                ", talla: " + producto.getTalla()));
    }

    private Pedido crearPedido(PagoRequest.ProductoPago producto, Pago pago, TallaConfiguracion talla) {
        Pedido pedido = new Pedido();
        pedido.setNombreProducto(talla.getProducto().getNombre());
        pedido.setCantidad(producto.getCantidad());
        pedido.setPagoProducto(producto.getPrecio());
        pedido.setTalla(producto.getTalla());
        pedido.setPago(pago);
        pedido.setProducto(talla.getProducto());
        return pedido;
    }

    @Transactional
    protected void actualizarStocks(List<PagoRequest.ProductoPago> productos) {
        productos.forEach(producto -> {
            TallaConfiguracion talla = obtenerTallaProducto(producto);
            talla.setStock(talla.getStock() - producto.getCantidad());
            tallaConfiguracionRepository.save(talla);
            productoService.actualizarStockProducto(producto.getProductoId());
        });
    }

    @Transactional(readOnly = true)
    public Optional<Pago> obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Pago obtenerPagoPorIdOrThrow(Long id) {
        return obtenerPagoPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Pago> obtenerTodosLosPagos() {
        return pagoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pago> obtenerPagosPorMetodo(String metodoPago) {
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("El método de pago no puede estar vacío");
        }
        return pagoRepository.findByMetodoPago(metodoPago);
    }

    @Transactional(readOnly = true)
    public List<Pago> buscarPagosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        return pagoRepository.findByFechaHoraBetween(fechaInicio, fechaFin);
    }

    @Transactional
    public void eliminarPago(Long id) {
        if (!pagoRepository.existsById(id)) {
            throw new IllegalArgumentException("Pago no encontrado con ID: " + id);
        }
        pagoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existePago(Long id) {
        return pagoRepository.existsById(id);
    }

    @Transactional
    public Pago actualizarPago(Long id, PagoRequest request) {
        validarPagoRequest(request);
        Pago pagoExistente = obtenerPagoPorIdOrThrow(id);
        pagoExistente.setMetodoPago(request.getMetodoPago());
        pagoExistente.setTotalPagado(request.getTotal());
        pagoExistente.setPermiteStockNegativo(true);
        return pagoRepository.save(pagoExistente);
    }
}