package com.example.PuntoVentaBack.Pedido.dto;

import java.math.BigDecimal;

public class ProductoPedidoDTO {
    private Long productoId;
    private String talla;
    private int cantidad;
    private BigDecimal precio;
    private String nombreProducto;
    private boolean permiteStockNegativo;

    // Getters and setters
    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public boolean isPermiteStockNegativo() {
        return permiteStockNegativo;
    }

    public void setPermiteStockNegativo(boolean permiteStockNegativo) {
        this.permiteStockNegativo = permiteStockNegativo;
    }
}