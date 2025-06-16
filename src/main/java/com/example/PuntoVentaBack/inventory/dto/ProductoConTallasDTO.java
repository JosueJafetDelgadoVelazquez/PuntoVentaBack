package com.example.PuntoVentaBack.inventory.dto;

import java.util.List;

public class ProductoConTallasDTO {
    public String nombre;
    public String codigoBarras;
    public int stock;
    public String descripcion;
    public String imagen;
    public String categoriaProducto;
    public String sexo;
    public Long idTallasCategoria;  // Mantenemos este campo
    public List<TallaPrecioDTO> tallas;

    public static class TallaPrecioDTO {
        public String talla;
        public double precio;
    }
}