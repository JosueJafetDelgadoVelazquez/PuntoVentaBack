package com.example.PuntoVentaBack.ticketbase.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket_base")
public class TicketBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreEmpresa;
    private String direccion;
    private String telefono;
    private String correo;
    private String sitioWeb;
    private String regimen;

    @Column(columnDefinition = "LONGTEXT")
    private String ticketLogo;

    // Getters y setters
    public Long getId() { return id; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getSitioWeb() { return sitioWeb; }
    public void setSitioWeb(String sitioWeb) { this.sitioWeb = sitioWeb; }

    public String getRegimen() { return regimen; }
    public void setRegimen(String regimen) { this.regimen = regimen; }

    public String getTicketLogo() { return ticketLogo; }
    public void setTicketLogo(String ticketLogo) { this.ticketLogo = ticketLogo; }
}