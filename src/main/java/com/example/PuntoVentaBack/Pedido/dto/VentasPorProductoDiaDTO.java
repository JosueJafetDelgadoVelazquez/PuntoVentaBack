package com.example.PuntoVentaBack.Pedido.dto;
// esto sirve para el reporte de ventas de los productos por di el que muestra lo que gana cada porocducto por dia
import java.math.BigDecimal;
import java.util.Date;

public class VentasPorProductoDiaDTO {
    private Date fecha;
    private String nombreProducto;
    private String talla;
    private Long cantidad;
    private BigDecimal total;

    public VentasPorProductoDiaDTO(Date fecha, String nombreProducto, String talla, Long cantidad, BigDecimal total) {
        this.fecha = fecha;
        this.nombreProducto = nombreProducto;
        this.talla = talla;
        this.cantidad = cantidad;
        this.total = total;
    }

    // Getters y setters
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    public Long getCantidad() { return cantidad; }
    public void setCantidad(Long cantidad) { this.cantidad = cantidad; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}