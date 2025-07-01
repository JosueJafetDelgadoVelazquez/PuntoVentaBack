package com.example.PuntoVentaBack.inventory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private String codigoBarras;
    private String descripcion;
    private String imagen;
    private String categoriaProducto;
    private String sexo;
    private Long idTallasCategoria;
    private boolean habilitado;
    private List<TallaConfiguracionDTO> tallas;
    private Integer stock;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public boolean isHabilitado() { return habilitado; }
    public void setHabilitado(boolean habilitado) { this.habilitado = habilitado; }
    public List<TallaConfiguracionDTO> getTallas() { return tallas; }
    public void setTallas(List<TallaConfiguracionDTO> tallas) { this.tallas = tallas; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}