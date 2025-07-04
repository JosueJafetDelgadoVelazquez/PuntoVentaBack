package com.example.PuntoVentaBack.ticketbase.control;

import com.example.PuntoVentaBack.ticketbase.model.TicketBase;
import com.example.PuntoVentaBack.ticketbase.model.TicketBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketBaseService {

    @Autowired
    private TicketBaseRepository repository;

    public TicketBase guardar(TicketBase ticketBase) {
        return repository.save(ticketBase);
    }

    public List<TicketBase> obtenerTodos() {
        return repository.findAll();
    }

    public TicketBase obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }
}
