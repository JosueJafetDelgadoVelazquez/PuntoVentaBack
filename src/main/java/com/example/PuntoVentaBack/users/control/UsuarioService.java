package com.example.PuntoVentaBack.users.control;

import com.example.PuntoVentaBack.users.model.Usuario;
import com.example.PuntoVentaBack.users.model.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    public List<Usuario> findByHabilitado(boolean habilitado) {
        return usuarioRepository.findByHabilitado(habilitado);
    }

    public Optional<Usuario> habilitarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setHabilitado(true);
                    return usuarioRepository.save(usuario);
                });
    }

    public Optional<Usuario> deshabilitarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setHabilitado(false);
                    return usuarioRepository.save(usuario);
                });
    }

    public Optional<Usuario> login(String correo, String contraseña) {
        return usuarioRepository.findByCorreo(correo)
                .filter(usuario -> usuario.getContraseña().equals(contraseña))
                .filter(Usuario::isHabilitado);
    }
}