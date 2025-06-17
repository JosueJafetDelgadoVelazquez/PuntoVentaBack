package com.example.PuntoVentaBack.users.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    List<Usuario> findByHabilitado(boolean habilitado);  // ✅ agregado para filtrar usuarios habilitados/deshabilitados
}
