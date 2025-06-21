// PedidoRepository.java
package com.example.PuntoVentaBack.Pedido.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByPagoId(Long pagoId);
}
