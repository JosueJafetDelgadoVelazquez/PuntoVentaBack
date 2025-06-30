package com.example.PuntoVentaBack.TallasConfiguracion.model;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TallaConfiguracionRepository extends JpaRepository<TallaConfiguracion, Long> {

    @Query("SELECT t FROM TallaConfiguracion t WHERE t.producto.id = :productoId AND t.talla = :talla")
    Optional<TallaConfiguracion> findByProductoIdAndTalla(@Param("productoId") Long productoId, @Param("talla") String talla);

    List<TallaConfiguracion> findByProductoId(Long productoId);
}