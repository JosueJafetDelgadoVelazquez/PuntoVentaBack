package com.example.PuntoVentaBack.ticketbase.control;

import com.example.PuntoVentaBack.ticketbase.model.TicketBase;
import com.example.PuntoVentaBack.ticketbase.model.TicketBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TicketBaseService {

    private final TicketBaseRepository repository;

    @Autowired
    public TicketBaseService(TicketBaseRepository repository) {
        this.repository = repository;
    }

    public TicketBase guardar(TicketBase ticketBase) {
        if (ticketBase.getHabilitado() != null && ticketBase.getHabilitado()) {
            repository.deshabilitarOtros(ticketBase.getId() != null ? ticketBase.getId() : -1L);
        }
        return repository.save(ticketBase);
    }

    @Transactional(readOnly = true)
    public List<TicketBase> obtenerTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public TicketBase obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TicketBase no encontrado con ID: " + id));
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TicketBase obtenerConfiguracionActiva() {
        return repository.findFirstByHabilitadoTrue()
                .orElseThrow(() -> new RuntimeException("No hay configuración de ticket activa"));
    }

    public TicketBase actualizarConfiguracion(Long id, TicketBase ticketBase) {
        ticketBase.setId(id);
        return guardar(ticketBase);
    }

    @Transactional(readOnly = true)
    public boolean existePorNombreEmpresa(String nombreEmpresa) {
        return repository.existsByNombreEmpresa(nombreEmpresa);
    }
}