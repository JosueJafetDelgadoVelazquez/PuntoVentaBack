package com.example.PuntoVentaBack.pagos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    @Query("SELECT p FROM Pago p WHERE p.metodoPago = :metodo")
    List<Pago> findByMetodoPago(@Param("metodo") String metodoPago);

    @Query("SELECT p FROM Pago p WHERE p.totalPagado > :montoMinimo")
    List<Pago> findByMontoMayorA(@Param("montoMinimo") double montoMinimo);

    @Query("SELECT COUNT(p) FROM Pago p WHERE p.metodoPago = :metodo")
    long countByMetodoPago(@Param("metodo") String metodoPago);

    @Query("SELECT p FROM Pago p WHERE p.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaHora ASC")
    List<Pago> findByFechaHoraBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}