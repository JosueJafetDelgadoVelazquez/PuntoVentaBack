package com.example.PuntoVentaBack.ticket.dto;

public class TicketRequest {
    private Long idPago;
    private Long idVendedor;
    private Long idTicketBase;
    private double cambio;
    private double impuestos;
    private double descuento;
    private String metodoPago;
    private String nombreVendedor;

    // Getters
    public Long getIdPago() {
        return idPago;
    }

    public Long getIdVendedor() {
        return idVendedor;
    }

    public Long getIdTicketBase() {
        return idTicketBase;
    }

    public double getCambio() {
        return cambio;
    }

    public double getImpuestos() {
        return impuestos;
    }

    public double getDescuento() {
        return descuento;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    // Setters con validaciones básicas
    public void setIdPago(Long idPago) {
        if (idPago == null) {
            throw new IllegalArgumentException("El ID de pago es requerido");
        }
        this.idPago = idPago;
    }

    public void setIdVendedor(Long idVendedor) {
        this.idVendedor = idVendedor;
    }

    public void setIdTicketBase(Long idTicketBase) {
        this.idTicketBase = idTicketBase;
    }

    public void setCambio(double cambio) {
        if (cambio < 0) {
            throw new IllegalArgumentException("El cambio no puede ser negativo");
        }
        this.cambio = cambio;
    }

    public void setImpuestos(double impuestos) {
        if (impuestos < 0) {
            throw new IllegalArgumentException("Los impuestos no pueden ser negativos");
        }
        this.impuestos = impuestos;
    }

    public void setDescuento(double descuento) {
        if (descuento < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo");
        }
        this.descuento = descuento;
    }

    public void setMetodoPago(String metodoPago) {
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("El método de pago es requerido");
        }
        if (metodoPago.length() < 2 || metodoPago.length() > 50) {
            throw new IllegalArgumentException("El método de pago debe tener entre 2 y 50 caracteres");
        }
        this.metodoPago = metodoPago;
    }

    public void setNombreVendedor(String nombreVendedor) {
        if (nombreVendedor != null && nombreVendedor.length() > 100) {
            throw new IllegalArgumentException("El nombre del vendedor no puede exceder 100 caracteres");
        }
        this.nombreVendedor = nombreVendedor;
    }

    // Método para validar todos los campos a la vez
    public void validar() {
        setIdPago(this.idPago);
        setMetodoPago(this.metodoPago);
        setCambio(this.cambio);
        setImpuestos(this.impuestos);
        setDescuento(this.descuento);

        // Validación adicional para ticket base
        if (this.idTicketBase == null) {
            throw new IllegalArgumentException("El ID de ticket base es requerido");
        }

        // Validación para vendedor (ID o nombre)
        if (this.idVendedor == null && (this.nombreVendedor == null || this.nombreVendedor.trim().isEmpty())) {
            throw new IllegalArgumentException("Se requiere un vendedor (ID o nombre)");
        }
    }
}