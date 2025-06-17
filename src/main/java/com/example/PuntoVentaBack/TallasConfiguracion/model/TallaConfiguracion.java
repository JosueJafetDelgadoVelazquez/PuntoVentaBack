package com.example.PuntoVentaBack.TallasConfiguracion.model;

import com.example.PuntoVentaBack.category.model.TallasCategoria;
import com.example.PuntoVentaBack.inventory.model.Producto;
import jakarta.persistence.*;

@Entity
@Table(name = "talla_configuracion")
public class TallaConfiguracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String talla;
    private double precio;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_tallas_categoria")
    private TallasCategoria tallaCategoria;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public TallasCategoria getTallaCategoria() { return tallaCategoria; }
    public void setTallaCategoria(TallasCategoria tallaCategoria) { this.tallaCategoria = tallaCategoria; }
}