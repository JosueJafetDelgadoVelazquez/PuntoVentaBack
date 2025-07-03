package com.example.PuntoVentaBack.inventory.model;

import com.example.PuntoVentaBack.category.model.TallasCategoria;
import com.example.PuntoVentaBack.TallasConfiguracion.model.TallaConfiguracion;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "productos")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "codigo_barras", nullable = false, unique = true)
    private String codigoBarras;

    private String descripcion;

    @Column(columnDefinition = "LONGTEXT")
    private String imagen;

    private String categoriaProducto;

    private String sexo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tallas_categoria")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TallasCategoria tallasCategoria;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean habilitado = true;

    @OneToMany(mappedBy = "producto", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = false)
    @JsonManagedReference
    private List<TallaConfiguracion> tallasConfiguracion = new ArrayList<>();

    @Column(nullable = false)
    private Integer stock = 0;

    // Constructores
    public Producto() {
    }

    public Producto(Long id) {
        this.id = id;
    }

    public Producto(String nombre, String codigoBarras) {
        this.nombre = nombre;
        this.codigoBarras = codigoBarras;
    }

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

    public List<TallaConfiguracion> getTallasConfiguracion() {
        return tallasConfiguracion;
    }

    public void setTallasConfiguracion(List<TallaConfiguracion> tallasConfiguracion) {
        this.tallasConfiguracion = tallasConfiguracion;
    }

    public Integer getStock() {
        if (tallasConfiguracion != null && !tallasConfiguracion.isEmpty()) {
            return tallasConfiguracion.stream().mapToInt(TallaConfiguracion::getStock).sum();
        }
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    // Métodos útiles
    public void agregarTallaConfiguracion(TallaConfiguracion tallaConfig) {
        tallaConfig.setProducto(this);
        this.tallasConfiguracion.add(tallaConfig);
    }

    public void removerTallaConfiguracion(TallaConfiguracion tallaConfig) {
        tallaConfig.setProducto(null);
        this.tallasConfiguracion.remove(tallaConfig);
    }

    @Transactional
    public void actualizarTallas(List<TallaConfiguracion> nuevasTallas) {
        // Mapa de tallas existentes por ID
        Map<Long, TallaConfiguracion> tallasExistentes = this.tallasConfiguracion.stream()
                .filter(t -> t.getId() != null)
                .collect(Collectors.toMap(TallaConfiguracion::getId, t -> t));

        // Procesar cada nueva talla
        for (TallaConfiguracion nuevaTalla : nuevasTallas) {
            if (nuevaTalla.getId() != null && tallasExistentes.containsKey(nuevaTalla.getId())) {
                // Actualizar talla existente
                TallaConfiguracion existente = tallasExistentes.get(nuevaTalla.getId());
                existente.setTalla(nuevaTalla.getTalla());
                existente.setPrecio(nuevaTalla.getPrecio());
                existente.setStock(nuevaTalla.getStock());
            } else {
                // Agregar nueva talla
                nuevaTalla.setProducto(this);
                this.tallasConfiguracion.add(nuevaTalla);
            }
        }

        // Eliminar tallas que ya no están en la lista
        Set<Long> idsTallasActuales = nuevasTallas.stream()
                .map(TallaConfiguracion::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        this.tallasConfiguracion.removeIf(t -> t.getId() != null && !idsTallasActuales.contains(t.getId()));
    }
}