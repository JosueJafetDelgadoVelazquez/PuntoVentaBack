package com.example.PuntoVentaBack.users.control;

import com.example.PuntoVentaBack.users.model.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        usuario.setHabilitado(true);
        return usuarioService.guardar(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);

            if (usuarioDetails.getNombre() != null) {
                usuario.setNombre(usuarioDetails.getNombre());
            }
            if (usuarioDetails.getCorreo() != null) {
                usuario.setCorreo(usuarioDetails.getCorreo());
            }
            if (usuarioDetails.getContraseña() != null && !usuarioDetails.getContraseña().isEmpty()) {
                usuario.setContraseña(usuarioDetails.getContraseña());
            }
            if (usuarioDetails.getRol() != null) {
                usuario.setRol(usuarioDetails.getRol());
            }

            return ResponseEntity.ok(usuarioService.guardar(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<Usuario> obtenerUsuarioPorCorreo(@PathVariable String correo) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(correo);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/habilitar")
    public ResponseEntity<Usuario> habilitarUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.habilitarUsuario(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/deshabilitar")
    public ResponseEntity<Usuario> deshabilitarUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.deshabilitarUsuario(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/estado/{habilitado}")
    public List<Usuario> obtenerUsuariosPorEstado(@PathVariable boolean habilitado) {
        return usuarioService.buscarPorEstado(habilitado);
    }
}