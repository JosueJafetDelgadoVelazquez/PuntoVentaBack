package com.example.PuntoVentaBack.TallasCategory.model;

import com.example.PuntoVentaBack.TallasCategory.model.TallaConfiguracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TallaConfiguracionRepository extends JpaRepository<TallaConfiguracion, Long> {
    List<TallaConfiguracion> findByProductoId(Long productoId);
}