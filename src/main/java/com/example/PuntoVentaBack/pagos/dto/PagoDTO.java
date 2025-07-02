package com.example.PuntoVentaBack.pagos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagoDTO {
    private String metodoPago;
    private double total;
    private Boolean permiteStockNegativo;
    private Double montoRecibido;  // Nuevo campo para pago en efectivo
    private Double cambio;         // Nuevo campo para pago en efectivo
    private List<ProductoPagoDTO> productos;

    public static class ProductoPagoDTO {
        private Long productoId;
        private Long tallaId;      // Nuevo campo para ID de talla
        private String talla;      // Mantenido para compatibilidad
        private int cantidad;
        private double precio;
        private String nombreProducto; // Nuevo campo para registro

        // Getters y Setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public Long getTallaId() { return tallaId; }
        public void setTallaId(Long tallaId) { this.tallaId = tallaId; }

        public String getTalla() { return talla; }
        public void setTalla(String talla) { this.talla = talla; }

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }

        public double getPrecio() { return precio; }
        public void setPrecio(double precio) { this.precio = precio; }

        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    }

    // Getters y Setters
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Boolean isPermiteStockNegativo() { return permiteStockNegativo; }
    public void setPermiteStockNegativo(Boolean permiteStockNegativo) {
        this.permiteStockNegativo = permiteStockNegativo;
    }

    public Double getMontoRecibido() { return montoRecibido; }
    public void setMontoRecibido(Double montoRecibido) { this.montoRecibido = montoRecibido; }

    public Double getCambio() { return cambio; }
    public void setCambio(Double cambio) { this.cambio = cambio; }

    public List<ProductoPagoDTO> getProductos() { return productos; }
    public void setProductos(List<ProductoPagoDTO> productos) { this.productos = productos; }
}