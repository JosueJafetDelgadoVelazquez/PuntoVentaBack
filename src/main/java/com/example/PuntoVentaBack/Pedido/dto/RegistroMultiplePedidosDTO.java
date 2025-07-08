package com.example.PuntoVentaBack.Pedido.dto;

import java.util.List;

public class RegistroMultiplePedidosDTO {
    private Long pagoId;
    private List<ProductoPedidoDTO> productos;

    // Getters y Setters
    public Long getPagoId() { return pagoId; }
    public void setPagoId(Long pagoId) { this.pagoId = pagoId; }
    public List<ProductoPedidoDTO> getProductos() { return productos; }
    public void setProductos(List<ProductoPedidoDTO> productos) { this.productos = productos; }
}