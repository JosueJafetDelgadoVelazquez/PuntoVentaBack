package com.example.PuntoVentaBack.pagos.dto;

import java.util.List;

public class PagoDTO {
    private String metodoPago; // Cambiado de metodo_pago a metodoPago
    private double total; // Nuevo campo para el total
    private Boolean permiteStockNegativo; // Nuevo campo para control de stock negativo
    private List<ProductoPagoDTO> productos; // Cambiado de PedidoDTO a ProductoPagoDTO

    public static class ProductoPagoDTO {
        private Long productoId; // Cambiado de idProducto a productoId
        private int cantidad;
        private double precio; // Nuevo campo
        private String talla; // Nuevo campo (antes era idTallaConfiguracion)

        // Getters y Setters
        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getPrecio() {
            return precio;
        }

        public void setPrecio(double precio) {
            this.precio = precio;
        }

        public String getTalla() {
            return talla;
        }

        public void setTalla(String talla) {
            this.talla = talla;
        }
    }

    // Getters y Setters
    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Boolean isPermiteStockNegativo() {
        return permiteStockNegativo;
    }

    public void setPermiteStockNegativo(Boolean permiteStockNegativo) {
        this.permiteStockNegativo = permiteStockNegativo;
    }

    public List<ProductoPagoDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoPagoDTO> productos) {
        this.productos = productos;
    }
}