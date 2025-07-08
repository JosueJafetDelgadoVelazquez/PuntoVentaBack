package com.example.PuntoVentaBack.pagos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagoDTO {
    private String metodoPago;
    private BigDecimal total = BigDecimal.ZERO;
    private Boolean permiteStockNegativo;
    private BigDecimal montoRecibido;  // Cambiado a BigDecimal
    private BigDecimal cambio;         // Cambiado a BigDecimal
    private List<ProductoPagoDTO> productos;

    public static class ProductoPagoDTO {
        private Long productoId;
        private Long tallaId;
        private String talla;
        private int cantidad;
        private BigDecimal precio;     // Cambiado a BigDecimal
        private String nombreProducto;

        // Getters y Setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public Long getTallaId() { return tallaId; }
        public void setTallaId(Long tallaId) { this.tallaId = tallaId; }

        public String getTalla() { return talla; }
        public void setTalla(String talla) { this.talla = talla; }

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecio() { return precio; }
        public void setPrecio(BigDecimal precio) { this.precio = precio; }

        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    }

    // Getters y Setters
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Boolean isPermiteStockNegativo() { return permiteStockNegativo; }
    public void setPermiteStockNegativo(Boolean permiteStockNegativo) {
        this.permiteStockNegativo = permiteStockNegativo;
    }

    public BigDecimal getMontoRecibido() { return montoRecibido; }
    public void setMontoRecibido(BigDecimal montoRecibido) { this.montoRecibido = montoRecibido; }

    public BigDecimal getCambio() { return cambio; }
    public void setCambio(BigDecimal cambio) { this.cambio = cambio; }

    public List<ProductoPagoDTO> getProductos() { return productos; }
    public void setProductos(List<ProductoPagoDTO> productos) { this.productos = productos; }
}