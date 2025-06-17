package com.example.PuntoVentaBack.inventory.model;

import com.example.PuntoVentaBack.category.model.TallasCategoria;
import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "codigo_barras", nullable = false, unique = true)
    private String codigoBarras;

    private int stock;

    private String descripcion;

    @Column(columnDefinition = "LONGTEXT")
    private String imagen;

    private String categoriaProducto;

    @Column(name = "sexo")
    private String sexo;

    @ManyToOne
    @JoinColumn(name = "id_tallas_categoria")
    private TallasCategoria tallasCategoria;

    @Column(nullable = false)
    private boolean habilitado = true; // Por defecto habilitado

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCategoriaProducto() {
        return categoriaProducto;
    }

    public void setCategoriaProducto(String categoriaProducto) {
        this.categoriaProducto = categoriaProducto;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public TallasCategoria getTallasCategoria() {
        return tallasCategoria;
    }

    public void setTallasCategoria(TallasCategoria tallasCategoria) {
        this.tallasCategoria = tallasCategoria;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
}
