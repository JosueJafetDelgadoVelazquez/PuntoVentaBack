package com.example.PuntoVentaBack.TallasConfiguracion.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TallaConfiguracionRepository extends JpaRepository<TallaConfiguracion, Long> {

    // Método para verificar existencia de talla por producto y nombre de talla
    boolean existsByProductoIdAndTalla(Long productoId, String talla);

    // Método para buscar configuración de talla por producto y talla
    @Query("SELECT t FROM TallaConfiguracion t WHERE t.producto.id = :productoId AND t.talla = :talla")
    Optional<TallaConfiguracion> findByProductoIdAndTalla(@Param("productoId") Long productoId, @Param("talla") String talla);

    // Método para obtener todas las configuraciones de talla de un producto
    List<TallaConfiguracion> findByProductoId(Long productoId);

    // Método para sumar el stock total de todas las tallas de un producto
    @Query("SELECT COALESCE(SUM(t.stock), 0) FROM TallaConfiguracion t WHERE t.producto.id = :productoId")
    int sumStockByProductoId(@Param("productoId") Long productoId);

    // Método para reducir stock con validación
    @Modifying
    @Transactional
    @Query("UPDATE TallaConfiguracion t SET t.stock = t.stock - :cantidad " +
            "WHERE t.producto.id = :productoId AND t.talla = :talla AND t.stock >= :cantidad")
    int reducirStock(@Param("productoId") Long productoId, @Param("talla") String talla, @Param("cantidad") int cantidad);

    // Método para aumentar stock
    @Modifying
    @Transactional
    @Query("UPDATE TallaConfiguracion t SET t.stock = t.stock + :cantidad " +
            "WHERE t.producto.id = :productoId AND t.talla = :talla")
    int aumentarStock(@Param("productoId") Long productoId, @Param("talla") String talla, @Param("cantidad") int cantidad);

    // Método para eliminar todas las tallas de un producto
    @Modifying
    @Transactional
    @Query("DELETE FROM TallaConfiguracion t WHERE t.producto.id = :productoId")
    void deleteByProductoId(@Param("productoId") Long productoId);

    // Método para buscar por ID de talla
    Optional<TallaConfiguracion> findById(Long id);
}