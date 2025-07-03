package com.example.PuntoVentaBack.TallasConfiguracion.model;

import com.example.PuntoVentaBack.category.model.TallasCategoria;
import com.example.PuntoVentaBack.inventory.model.Producto;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "talla_configuracion")
public class TallaConfiguracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String talla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    @JsonBackReference
    private Producto producto;

    @Column(nullable = false)
    private double precio;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tallas_categoria")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TallasCategoria tallaCategoria;

    @Transient
    private boolean eliminar;

    // Constructores
    public TallaConfiguracion() {
    }

    public TallaConfiguracion(String talla, Producto producto, double precio, int stock, TallasCategoria tallaCategoria) {
        this.talla = talla;
        this.producto = producto;
        this.precio = precio;
        this.stock = stock;
        this.tallaCategoria = tallaCategoria;
        this.eliminar = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public TallasCategoria getTallaCategoria() {
        return tallaCategoria;
    }

    public void setTallaCategoria(TallasCategoria tallaCategoria) {
        this.tallaCategoria = tallaCategoria;
    }

    public boolean isEliminar() {
        return eliminar;
    }

    public void setEliminar(boolean eliminar) {
        this.eliminar = eliminar;
    }

    // Método para facilitar la creación de DTOs
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("talla", this.talla);
        map.put("precio", this.precio);
        map.put("stock", this.stock);
        map.put("productoId", this.producto != null ? this.producto.getId() : null);
        map.put("tallaCategoriaId", this.tallaCategoria != null ? this.tallaCategoria.getId() : null);
        map.put("eliminar", this.eliminar);
        return map;
    }
}