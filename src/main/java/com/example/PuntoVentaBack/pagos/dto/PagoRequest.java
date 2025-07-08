package com.example.PuntoVentaBack.pagos.dto;

import java.math.BigDecimal;
import java.util.List;

public class PagoRequest {
    private String metodoPago;
    private BigDecimal total;  // Cambiado de double a BigDecimal
    private boolean permiteStockNegativo;
    private List<ProductoPago> productos;

    public static class ProductoPago {
        private Long productoId;
        private String talla;
        private int cantidad;
        private BigDecimal precio;  // Cambiado de double a BigDecimal

        // Getters y Setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public String getTalla() { return talla; }
        public void setTalla(String talla) { this.talla = talla; }

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecio() { return precio; }
        public void setPrecio(BigDecimal precio) { this.precio = precio; }
    }

    // Getters y Setters
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public boolean isPermiteStockNegativo() { return permiteStockNegativo; }
    public void setPermiteStockNegativo(boolean permiteStockNegativo) {
        this.permiteStockNegativo = permiteStockNegativo;
    }

    public List<ProductoPago> getProductos() { return productos; }
    public void setProductos(List<ProductoPago> productos) { this.productos = productos; }
}