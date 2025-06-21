package com.example.PuntoVentaBack.pagos.model;

import com.example.PuntoVentaBack.Pedido.model.Pedido;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @Column(name = "total_pagado", nullable = false)
    private double totalPagado;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos;

    // Constructores
    public Pago() {
        this.fechaHora = LocalDateTime.now();
    }

    public Pago(String metodoPago, double totalPagado) {
        this();
        this.metodoPago = metodoPago;
        this.totalPagado = totalPagado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(double totalPagado) {
        this.totalPagado = totalPagado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}