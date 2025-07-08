package com.example.PuntoVentaBack.users.control;

import com.example.PuntoVentaBack.users.model.Usuario;
import com.example.PuntoVentaBack.users.model.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Métodos para obtener usuarios (versiones que lanzan excepción)
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
    }

    // Métodos para buscar (devuelven Optional)
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    // Métodos CRUD básicos
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        if (usuario.getId() != null && usuarioRepository.existsById(usuario.getId())) {
            throw new RuntimeException("Usuario ya existe con ID: " + usuario.getId());
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarPorId(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    // Métodos de estado
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorEstado(boolean habilitado) {
        return usuarioRepository.findByHabilitado(habilitado);
    }

    @Transactional
    public Usuario habilitarUsuario(Long id) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setHabilitado(true);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario deshabilitarUsuario(Long id) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setHabilitado(false);
        return usuarioRepository.save(usuario);
    }

    // Métodos de verificación
    @Transactional(readOnly = true)
    public boolean existePorCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }
}