package com.example.PuntoVentaBack.inventory.model;

import com.example.PuntoVentaBack.inventory.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}