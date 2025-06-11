package com.example.PuntoVentaBack.category.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TallasCategoriaRepository extends JpaRepository<TallasCategoria, Long> {
    Optional<TallasCategoria> findByNombre(String nombre);
}