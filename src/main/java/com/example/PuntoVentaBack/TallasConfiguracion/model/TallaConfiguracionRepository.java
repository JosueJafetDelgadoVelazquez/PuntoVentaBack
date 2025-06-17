package com.example.PuntoVentaBack.TallasConfiguracion.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TallaConfiguracionRepository extends JpaRepository<TallaConfiguracion, Long> {
    List<TallaConfiguracion> findByProductoId(Long productoId);
}