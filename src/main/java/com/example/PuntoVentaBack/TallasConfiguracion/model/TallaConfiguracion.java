package com.example.PuntoVentaBack.TallasConfiguracion.model;

import com.example.PuntoVentaBack.category.model.TallasCategoria;
import com.example.PuntoVentaBack.inventory.model.Producto;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

@Entity
@Table(name = "talla_configuracion")
public class TallaConfiguracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String talla;
    private double precio;
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    @JsonBackReference
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tallas_categoria")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TallasCategoria tallaCategoria;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public TallasCategoria getTallaCategoria() { return tallaCategoria; }
    public void setTallaCategoria(TallasCategoria tallaCategoria) { this.tallaCategoria = tallaCategoria; }
}