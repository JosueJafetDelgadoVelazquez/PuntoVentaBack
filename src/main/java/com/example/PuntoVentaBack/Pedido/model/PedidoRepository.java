package com.example.PuntoVentaBack.Pedido.model;

import com.example.PuntoVentaBack.Pedido.dto.VentasPorProductoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByPagoId(Long pagoId);

    @Query("SELECT p FROM Pedido p WHERE p.producto.id = :productoId")
    List<Pedido> findByProductoId(@Param("productoId") Long productoId);

    boolean existsByPagoId(Long pagoId);

    long countByProductoId(Long productoId);

    @Query("SELECT p.producto.id, p.nombreProducto, p.talla, " +
            "SUM(p.cantidad), SUM(p.pagoProducto), MAX(p.pago.fechaHora) " +
            "FROM Pedido p " +
            "WHERE p.pago.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY p.producto.id, p.nombreProducto, p.talla")
    List<Object[]> findVentasAgrupadasPorProducto(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}