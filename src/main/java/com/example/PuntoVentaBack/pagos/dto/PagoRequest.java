package com.example.PuntoVentaBack.pagos.dto;

import java.util.List;

public class PagoRequest {
    private String metodoPago;
    private double total;
    private boolean permiteStockNegativo;
    private List<ProductoPago> productos;

    public static class ProductoPago {
        private Long productoId;
        private int cantidad;
        private double precio;
        private String talla;

        // Getters y Setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public double getPrecio() { return precio; }
        public void setPrecio(double precio) { this.precio = precio; }
        public String getTalla() { return talla; }
        public void setTalla(String talla) { this.talla = talla; }
    }

    // Getters y Setters
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public boolean isPermiteStockNegativo() { return permiteStockNegativo; }
    public void setPermiteStockNegativo(boolean permiteStockNegativo) { this.permiteStockNegativo = permiteStockNegativo; }
    public List<ProductoPago> getProductos() { return productos; }
    public void setProductos(List<ProductoPago> productos) { this.productos = productos; }
}