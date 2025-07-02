package com.example.PuntoVentaBack.inventory.dto;

import java.util.List;

public class ProductoConTallasDTO {
    private String nombre;
    private String codigoBarras;
    private String descripcion;
    private String imagen;
    private String categoriaProducto;
    private String sexo;
    private Long idTallasCategoria;
    private List<TallaPrecioStockDTO> tallas;

    public static class TallaPrecioStockDTO {
        private Long id;  // Added ID field
        private String talla;
        private double precio;
        private int stock;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTalla() { return talla; }
        public void setTalla(String talla) { this.talla = talla; }
        public double getPrecio() { return precio; }
        public void setPrecio(double precio) { this.precio = precio; }
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public String getCategoriaProducto() { return categoriaProducto; }
    public void setCategoriaProducto(String categoriaProducto) { this.categoriaProducto = categoriaProducto; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public Long getIdTallasCategoria() { return idTallasCategoria; }
    public void setIdTallasCategoria(Long idTallasCategoria) { this.idTallasCategoria = idTallasCategoria; }
    public List<TallaPrecioStockDTO> getTallas() { return tallas; }
    public void setTallas(List<TallaPrecioStockDTO> tallas) { this.tallas = tallas; }
}