package com.example.PuntoVentaBack.ticket.control;

import com.example.PuntoVentaBack.pagos.model.Pago;
import com.example.PuntoVentaBack.ticket.model.Ticket;
import com.example.PuntoVentaBack.ticket.model.TicketRepository;
import com.example.PuntoVentaBack.users.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket crearTicket(Pago pago, Usuario vendedor, double cambio, double impuestos, double descuento, String metodoPago) {
        Ticket ticket = new Ticket();
        ticket.setPago(pago);
        ticket.setVendedor(vendedor);
        ticket.setNombreVendedor(vendedor.getNombre());
        ticket.setCambio(cambio);
        ticket.setImpuestos(impuestos);
        ticket.setDescuento(descuento);
        ticket.setMetodoPago(metodoPago);
        return ticketRepository.save(ticket);
    }
}
