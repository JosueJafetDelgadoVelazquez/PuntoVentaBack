package com.example.PuntoVentaBack.ticketbase.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TicketBaseRepository extends JpaRepository<TicketBase, Long> {

    @Query("SELECT t FROM TicketBase t WHERE t.habilitado = true")
    Optional<TicketBase> findFirstByHabilitadoTrue();

    @Query("SELECT t FROM TicketBase t WHERE t.nombreEmpresa LIKE %:nombre%")
    List<TicketBase> buscarPorNombreEmpresa(@Param("nombre") String nombre);

    boolean existsByNombreEmpresa(String nombreEmpresa);

    @Transactional
    @Modifying
    @Query("UPDATE TicketBase t SET t.habilitado = false WHERE t.habilitado = true AND t.id != :idExcluido")
    void deshabilitarOtros(@Param("idExcluido") Long idExcluido);
}