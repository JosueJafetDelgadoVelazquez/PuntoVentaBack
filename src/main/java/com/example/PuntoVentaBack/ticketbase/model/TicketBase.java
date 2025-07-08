package com.example.PuntoVentaBack.ticketbase.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket_base")
public class TicketBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_empresa", nullable = false, length = 150)
    private String nombreEmpresa;  // Nota: nombre_empresa en DB, nombreEmpresa en Java

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "sitio_web", length = 100)
    private String sitioWeb;

    @Column(name = "regimen", length = 100)
    private String regimen;

    @Column(name = "ticket_logo", columnDefinition = "LONGTEXT")
    private String ticketLogo;

    @Column(name = "habilitado")
    private Boolean habilitado = false;
    // Constructor
    public TicketBase() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public String getRegimen() {
        return regimen;
    }

    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    public String getTicketLogo() {
        return ticketLogo;
    }

    public void setTicketLogo(String ticketLogo) {
        this.ticketLogo = ticketLogo;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TicketBase)) return false;
        TicketBase that = (TicketBase) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "TicketBase{" +
                "id=" + id +
                ", nombreEmpresa='" + nombreEmpresa + '\'' +
                ", direccion='" + direccion + '\'' +
                ", habilitado=" + habilitado +
                '}';
    }
}