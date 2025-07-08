package com.example.PuntoVentaBack.TallasConfiguracion.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TallaConfiguracionRepository extends JpaRepository<TallaConfiguracion, Long> {

    // Verifica si existe una talla para un producto específico
    boolean existsByProductoIdAndTalla(Long productoId, String talla);

    // Busca una talla específica por producto y nombre de talla
    @Query("SELECT t FROM TallaConfiguracion t WHERE t.producto.id = :productoId AND t.talla = :talla")
    Optional<TallaConfiguracion> findByProductoIdAndTalla(@Param("productoId") Long productoId, @Param("talla") String talla);

    // Obtiene todas las tallas de un producto
    @Query("SELECT t FROM TallaConfiguracion t WHERE t.producto.id = :productoId ORDER BY t.talla")
    List<TallaConfiguracion> findByProductoId(@Param("productoId") Long productoId);

    // Suma el stock total de todas las tallas de un producto
    @Query("SELECT COALESCE(SUM(t.stock), 0) FROM TallaConfiguracion t WHERE t.producto.id = :productoId")
    int sumStockByProductoId(@Param("productoId") Long productoId);

    // Reduce el stock con validación de stock suficiente
    @Modifying
    @Transactional
    @Query("UPDATE TallaConfiguracion t SET t.stock = t.stock - :cantidad WHERE t.id = :id")
    int reducirStockPorId(@Param("id") Long id, @Param("cantidad") int cantidad);


    // Aumenta el stock de una talla específica
    @Modifying
    @Transactional
    @Query("UPDATE TallaConfiguracion t SET t.stock = t.stock + :cantidad " +
            "WHERE t.id = :id")
    int aumentarStockPorId(@Param("id") Long id, @Param("cantidad") int cantidad);

    // Elimina todas las tallas de un producto
    @Modifying
    @Transactional
    @Query("DELETE FROM TallaConfiguracion t WHERE t.producto.id = :productoId")
    void deleteByProductoId(@Param("productoId") Long productoId);

    // Busca por ID de talla
    @Query("SELECT t FROM TallaConfiguracion t WHERE t.id = :id")
    Optional<TallaConfiguracion> findById(@Param("id") Long id);

    // Método para buscar tallas por producto y lista de nombres de talla
    @Query("SELECT t FROM TallaConfiguracion t WHERE t.producto.id = :productoId AND t.talla IN :tallas")
    List<TallaConfiguracion> findByProductoIdAndTallaIn(@Param("productoId") Long productoId,
                                                        @Param("tallas") List<String> tallas);

    // Método para actualizar precio de una talla
    @Modifying
    @Transactional
    @Query("UPDATE TallaConfiguracion t SET t.precio = :precio WHERE t.id = :id")
    int actualizarPrecio(@Param("id") Long id, @Param("precio") double precio);

    // Método para actualizar múltiples tallas en una sola operación
    @Modifying
    @Transactional
    @Query("UPDATE TallaConfiguracion t SET t.precio = :precio, t.stock = :stock " +
            "WHERE t.id = :id")
    int actualizarTallaCompleta(@Param("id") Long id,
                                @Param("precio") double precio,
                                @Param("stock") int stock);

    // Método para obtener tallas con stock bajo
    @Query("SELECT t FROM TallaConfiguracion t WHERE t.producto.id = :productoId AND t.stock <= :umbral")
    List<TallaConfiguracion> findTallasConStockBajo(@Param("productoId") Long productoId,
                                                    @Param("umbral") int umbral);
}