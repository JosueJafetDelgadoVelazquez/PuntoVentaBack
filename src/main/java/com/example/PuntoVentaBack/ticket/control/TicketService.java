package com.example.PuntoVentaBack.ticket.control;

import com.example.PuntoVentaBack.pagos.model.Pago;
import com.example.PuntoVentaBack.pagos.control.PagoService;
import com.example.PuntoVentaBack.ticket.dto.TicketRequest;
import com.example.PuntoVentaBack.ticket.model.Ticket;
import com.example.PuntoVentaBack.ticket.model.TicketRepository;
import com.example.PuntoVentaBack.ticketbase.model.TicketBase;
import com.example.PuntoVentaBack.ticketbase.control.TicketBaseService;
import com.example.PuntoVentaBack.users.model.Usuario;
import com.example.PuntoVentaBack.users.control.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final PagoService pagoService;
    private final UsuarioService usuarioService;
    private final TicketBaseService ticketBaseService;

    public TicketService(TicketRepository ticketRepository,
                         PagoService pagoService,
                         UsuarioService usuarioService,
                         TicketBaseService ticketBaseService) {
        this.ticketRepository = ticketRepository;
        this.pagoService = pagoService;
        this.usuarioService = usuarioService;
        this.ticketBaseService = ticketBaseService;
    }

    @Transactional
    public Ticket crearTicket(TicketRequest request) {
        logger.info("Iniciando creación de ticket con request: {}", request);

        if (request == null) {
            throw new IllegalArgumentException("TicketRequest no puede ser nulo");
        }

        try {
            request.validar();

            Pago pago = obtenerPagoValidado(request.getIdPago());
            Usuario vendedor = obtenerVendedorValidado(request.getIdVendedor());
            TicketBase ticketBase = obtenerTicketBaseValidado(request.getIdTicketBase());

            if (ticketBase == null) {
                throw new IllegalStateException("No se encontró configuración de ticket base válida");
            }

            Ticket ticket = construirTicket(request, pago, vendedor, ticketBase);
            logger.info("Ticket construido correctamente: {}", ticket);

            return guardarTicket(ticket);
        } catch (Exception e) {
            logger.error("Error al crear ticket: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Pago obtenerPagoValidado(Long idPago) {
        if (idPago == null) {
            throw new IllegalArgumentException("El ID de pago no puede ser nulo");
        }
        return pagoService.obtenerPagoPorId(idPago)
                .orElseThrow(() -> new RuntimeException("No se encontró el pago con ID: " + idPago));
    }

    private Usuario obtenerVendedorValidado(Long idVendedor) {
        if (idVendedor == null) {
            logger.warn("ID de vendedor es nulo, se usará 'Sistema' como vendedor");
            return null;
        }
        Usuario vendedor = usuarioService.obtenerUsuarioPorId(idVendedor);
        if (vendedor != null && !vendedor.isHabilitado()) {
            throw new IllegalStateException("El vendedor no está habilitado");
        }
        return vendedor;
    }

    private TicketBase obtenerTicketBaseValidado(Long idTicketBase) {
        if (idTicketBase == null) {
            logger.info("Obteniendo configuración activa de ticket base");
            return ticketBaseService.obtenerConfiguracionActiva();
        }
        logger.info("Buscando ticket base con ID: {}", idTicketBase);
        TicketBase ticketBase = ticketBaseService.obtenerPorId(idTicketBase);
        if (ticketBase != null && !ticketBase.getHabilitado()) {
            throw new IllegalStateException("La configuración de ticket no está habilitada");
        }
        return ticketBase;
    }

    private Ticket construirTicket(TicketRequest request, Pago pago, Usuario vendedor, TicketBase ticketBase) {
        Ticket ticket = new Ticket();
        ticket.setPago(pago);
        ticket.setVendedor(vendedor);
        ticket.setTicketBase(ticketBase);

        // Manejo mejorado del nombre del vendedor
        String nombreVendedor = "Sistema";
        if (vendedor != null) {
            nombreVendedor = vendedor.getNombre();
        } else if (request.getNombreVendedor() != null && !request.getNombreVendedor().trim().isEmpty()) {
            nombreVendedor = request.getNombreVendedor();
        }
        ticket.setNombreVendedor(nombreVendedor);

        ticket.setCambio(request.getCambio());
        ticket.setImpuestos(BigDecimal.valueOf(request.getImpuestos()));
        ticket.setDescuento(BigDecimal.valueOf(request.getDescuento()));
        ticket.setMetodoPago(request.getMetodoPago());

        return ticket;
    }

    private Ticket guardarTicket(Ticket ticket) {
        try {
            Ticket savedTicket = ticketRepository.save(ticket);
            logger.info("Ticket guardado exitosamente con ID: {}", savedTicket.getId());
            return savedTicket;
        } catch (Exception e) {
            logger.error("Error al guardar ticket: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el ticket en la base de datos", e);
        }
    }

    @Transactional(readOnly = true)
    public Ticket obtenerTicketPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del ticket no puede ser nulo");
        }
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Ticket> obtenerTicketsPorPago(Long pagoId) {
        if (pagoId == null) {
            throw new IllegalArgumentException("El ID de pago no puede ser nulo");
        }
        return ticketRepository.findByPagoId(pagoId);
    }

    @Transactional(readOnly = true)
    public List<Ticket> obtenerTicketsPorVendedor(Long vendedorId) {
        if (vendedorId == null) {
            throw new IllegalArgumentException("El ID de vendedor no puede ser nulo");
        }
        return ticketRepository.findByVendedorId(vendedorId);
    }

    @Transactional(readOnly = true)
    public Optional<TicketBase> obtenerConfiguracionTicketActiva() {
        try {
            TicketBase ticketBase = ticketBaseService.obtenerConfiguracionActiva();
            return Optional.ofNullable(ticketBase);
        } catch (RuntimeException e) {
            logger.warn("No se pudo obtener la configuración activa de ticket: {}", e.getMessage());
            return Optional.empty();
        }
    }
}