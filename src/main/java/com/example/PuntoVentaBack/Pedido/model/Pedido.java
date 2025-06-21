package com.example.PuntoVentaBack.Pedido.model;

import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracion;
import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.pagos.model.Pago;
import jakarta.persistence.*;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreProducto;

    private int cantidad;

    private double pagoProducto;

    @ManyToOne
    @JoinColumn(name = "id_pago")
    private Pago pago;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_talla_configuracion")
    private TallaConfiguracion tallaConfiguracion;

    // Getters y Setters

    public Long getId() { return id; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPagoProducto() { return pagoProducto; }
    public void setPagoProducto(double pagoProducto) { this.pagoProducto = pagoProducto; }

    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public TallaConfiguracion getTallaConfiguracion() { return tallaConfiguracion; }
    public void setTallaConfiguracion(TallaConfiguracion tallaConfiguracion) { this.tallaConfiguracion = tallaConfiguracion; }
}
