package com.example.PuntoVentaBack.TallasConfiguracion.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface TallaConfiguracionRepository extends JpaRepository<TallaConfiguracion, Long> {

    @Query("SELECT t FROM TallaConfiguracion t WHERE t.producto.id = :productoId AND t.talla = :talla")
    Optional<TallaConfiguracion> findByProductoIdAndTalla(@Param("productoId") Long productoId, @Param("talla") String talla);

    List<TallaConfiguracion> findByProductoId(Long productoId);

    @Query("SELECT COALESCE(SUM(t.stock), 0) FROM TallaConfiguracion t WHERE t.producto.id = :productoId")
    int sumStockByProductoId(@Param("productoId") Long productoId);

    @Modifying
    @Transactional
    @Query("UPDATE TallaConfiguracion t SET t.stock = t.stock - :cantidad WHERE t.producto.id = :productoId AND t.talla = :talla AND t.stock >= :cantidad")
    int reducirStock(@Param("productoId") Long productoId, @Param("talla") String talla, @Param("cantidad") int cantidad);

    @Modifying
    @Transactional
    @Query("UPDATE TallaConfiguracion t SET t.stock = t.stock + :cantidad WHERE t.producto.id = :productoId AND t.talla = :talla")
    int aumentarStock(@Param("productoId") Long productoId, @Param("talla") String talla, @Param("cantidad") int cantidad);

    @Modifying
    @Transactional
    @Query("DELETE FROM TallaConfiguracion t WHERE t.producto.id = :productoId")
    void deleteByProductoId(@Param("productoId") Long productoId);
}