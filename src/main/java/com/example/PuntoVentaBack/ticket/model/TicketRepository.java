package com.example.PuntoVentaBack.ticket.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByPagoId(Long pagoId);
    List<Ticket> findByVendedorId(Long vendedorId);
}