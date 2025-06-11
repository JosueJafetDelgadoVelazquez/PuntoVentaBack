package com.example.PuntoVentaBack.inventory.model;

import com.example.PuntoVentaBack.category.model.TallasCategoria;
import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String codigoBarras;
    private int stock;
    private String descripcion;

    @Column(columnDefinition = "LONGTEXT")
    private String imagen;

    private String categoriaProducto; // Nueva columna para Empresa, Escuela, etc.

    @ManyToOne
    @JoinColumn(name = "id_tallas_categoria")
    private TallasCategoria tallasCategoria;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getCategoriaProducto() { return categoriaProducto; }
    public void setCategoriaProducto(String categoriaProducto) { this.categoriaProducto = categoriaProducto; }

    public TallasCategoria getTallasCategoria() {
        return tallasCategoria;
    }

    public void setTallasCategoria(TallasCategoria tallasCategoria) {
        this.tallasCategoria = tallasCategoria;
    }
}
