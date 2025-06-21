package com.example.PuntoVentaBack.pagos.dto;

import java.util.List;

public class PagoDTO {
    private String metodo_pago;
    private List<PedidoDTO> productos;

    public static class PedidoDTO {
        private Long idProducto;
        private Long idTallaConfiguracion;
        private String nombreProducto;
        private int cantidad;

        // Getters y Setters
        public Long getIdProducto() {
            return idProducto;
        }

        public void setIdProducto(Long idProducto) {
            this.idProducto = idProducto;
        }

        public Long getIdTallaConfiguracion() {
            return idTallaConfiguracion;
        }

        public void setIdTallaConfiguracion(Long idTallaConfiguracion) {
            this.idTallaConfiguracion = idTallaConfiguracion;
        }

        public String getNombreProducto() {
            return nombreProducto;
        }

        public void setNombreProducto(String nombreProducto) {
            this.nombreProducto = nombreProducto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
    }

    // Getters y Setters
    public String getMetodo_pago() {
        return metodo_pago;
    }

    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public List<PedidoDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<PedidoDTO> productos) {
        this.productos = productos;
    }
}