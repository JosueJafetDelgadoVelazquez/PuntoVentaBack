package com.example.PuntoVentaBack.TallasCategory.control;

import com.example.PuntoVentaBack.TallasCategory.model.TallaConfiguracion;
import com.example.PuntoVentaBack.TallasCategory.model.TallaConfiguracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talla-configuracion")
public class TallaConfiguracionController {

    @Autowired
    private TallaConfiguracionRepository repository;

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@RequestBody TallaConfiguracion configuracion) {
        return ResponseEntity.ok(repository.save(configuracion));
    }
}