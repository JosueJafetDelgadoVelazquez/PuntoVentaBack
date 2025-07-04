package com.example.PuntoVentaBack.ticketbase.control;

import com.example.PuntoVentaBack.ticketbase.model.TicketBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-base")
public class TicketBaseController {

    @Autowired
    private TicketBaseService service;

    @PostMapping
    public TicketBase crear(@RequestBody TicketBase ticketBase) {
        return service.guardar(ticketBase);
    }

    @GetMapping
    public List<TicketBase> listar() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public TicketBase obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }
}
