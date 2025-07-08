package com.example.PuntoVentaBack.Pedido.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Método para buscar pedidos por pago
    List<Pedido> findByPagoId(Long pagoId);

    // Método para buscar pedidos por producto (usando @Query)
    @Query("SELECT p FROM Pedido p WHERE p.producto.id = :productoId")
    List<Pedido> findByProductoId(@Param("productoId") Long productoId);

    // Métodos adicionales útiles
    boolean existsByPagoId(Long pagoId);
    long countByProductoId(Long productoId);
}