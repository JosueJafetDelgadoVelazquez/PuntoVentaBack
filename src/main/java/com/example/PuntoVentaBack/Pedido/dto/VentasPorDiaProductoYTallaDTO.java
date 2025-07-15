package com.example.PuntoVentaBack.Pedido.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VentasPorDiaProductoYTallaDTO {
    private LocalDate fecha;
    private String nombreProducto;
    private String talla;
    private Integer cantidadVendida;
    private BigDecimal totalVendido;
    private BigDecimal precioPromedio;

    public VentasPorDiaProductoYTallaDTO(LocalDate fecha, String nombreProducto, String talla,
                                         Integer cantidadVendida, BigDecimal totalVendido,
                                         BigDecimal precioPromedio) {
        this.fecha = fecha;
        this.nombreProducto = nombreProducto;
        this.talla = talla;
        this.cantidadVendida = cantidadVendida;
        this.totalVendido = totalVendido;
        this.precioPromedio = precioPromedio;
    }

    // Getters
    public LocalDate getFecha() { return fecha; }
    public String getNombreProducto() { return nombreProducto; }
    public String getTalla() { return talla; }
    public Integer getCantidadVendida() { return cantidadVendida; }
    public BigDecimal getTotalVendido() { return totalVendido; }
    public BigDecimal getPrecioPromedio() { return precioPromedio; }
}