package com.example.PuntoVentaBack.ticket.control;

import com.example.PuntoVentaBack.ticket.dto.TicketRequest;
import com.example.PuntoVentaBack.pagos.model.Pago;
import com.example.PuntoVentaBack.pagos.model.PagoRepository;
import com.example.PuntoVentaBack.ticket.model.Ticket;
import com.example.PuntoVentaBack.users.model.Usuario;
import com.example.PuntoVentaBack.users.model.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public Ticket crearTicket(@RequestBody TicketRequest request) {
        Pago pago = pagoRepository.findById(request.getIdPago())
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        Usuario vendedor = usuarioRepository.findById(request.getIdVendedor())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ticketService.crearTicket(
                pago,
                vendedor,
                request.getCambio(),
                request.getImpuestos(),
                request.getDescuento(),
                request.getMetodoPago()
        );
    }
}
