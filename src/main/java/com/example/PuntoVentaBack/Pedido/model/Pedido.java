package com.example.PuntoVentaBack.Pedido.model;

import com.example.PuntoVentaBack.inventory.model.Producto;
import com.example.PuntoVentaBack.pagos.model.Pago;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombreProducto;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pagoProducto;

    @Column(length = 20)
    private String talla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        this.cantidad = cantidad;
    }

    public BigDecimal getPagoProducto() {
        return pagoProducto;
    }

    public void setPagoProducto(BigDecimal pagoProducto) {
        if (pagoProducto == null || pagoProducto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El pago del producto debe ser mayor a cero");
        }
        this.pagoProducto = pagoProducto;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser nulo");
        }
        this.pago = pago;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        this.producto = producto;
    }

    public BigDecimal calculateTotal() {
        return pagoProducto.multiply(new BigDecimal(cantidad));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido)) return false;
        Pedido pedido = (Pedido) o;
        return id != null && id.equals(pedido.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", cantidad=" + cantidad +
                ", pagoProducto=" + pagoProducto +
                '}';
    }
}
