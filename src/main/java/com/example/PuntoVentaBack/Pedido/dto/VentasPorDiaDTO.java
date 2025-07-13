// En el paquete com.example.PuntoVentaBack.Pedido.dto
package com.example.PuntoVentaBack.Pedido.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VentasPorDiaDTO {
    private LocalDate fecha;
    private BigDecimal totalVendido;
    private Integer transacciones;

    public VentasPorDiaDTO(LocalDate fecha, BigDecimal totalVendido, Integer transacciones) {
        this.fecha = fecha;
        this.totalVendido = totalVendido;
        this.transacciones = transacciones;
    }

    // Getters y Setters
    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getTotalVendido() {
        return totalVendido;
    }

    public Integer getTransacciones() {
        return transacciones;
    }
}