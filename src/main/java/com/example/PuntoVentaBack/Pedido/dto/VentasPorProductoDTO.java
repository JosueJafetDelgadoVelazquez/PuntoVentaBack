package com.example.PuntoVentaBack.Pedido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentasPorProductoDTO {
    private Long productoId;
    private String producto;
    private String talla;
    private Integer cantidadVendida;
    private BigDecimal totalVendido;
    private LocalDateTime fecha;

    // Constructor completo que coincide con la consulta JPQL
    public VentasPorProductoDTO(Long productoId, String producto, String talla,
                                Integer cantidadVendida, BigDecimal totalVendido,
                                LocalDateTime fecha) {
        this.productoId = productoId;
        this.producto = producto;
        this.talla = talla;
        this.cantidadVendida = cantidadVendida;
        this.totalVendido = totalVendido;
        this.fecha = fecha;
    }

    // Getters
    public Long getProductoId() { return productoId; }
    public String getProducto() { return producto; }
    public String getTalla() { return talla; }
    public Integer getCantidadVendida() { return cantidadVendida; }
    public BigDecimal getTotalVendido() { return totalVendido; }
    public LocalDateTime getFecha() { return fecha; }
}