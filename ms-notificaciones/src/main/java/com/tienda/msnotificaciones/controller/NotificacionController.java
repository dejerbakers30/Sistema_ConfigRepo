package com.tienda.msnotificaciones.controller;

import com.tienda.msnotificaciones.model.NotificacionLog;
import com.tienda.msnotificaciones.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NotificacionController {

    private static final Logger log = LoggerFactory.getLogger(NotificacionController.class);

    private final NotificacionRepository notificacionRepository;

    @Value("${notif.canal:consola}")
    private String canal;

    @Value("${notif.modo-simulado:true}")
    private Boolean modoSimulado;

    @Value("${mensaje.global:Mensaje global por defecto}")
    private String mensajeGlobal;

    public NotificacionController(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    // 1. GET /logs (y /notificaciones/logs)
    @GetMapping({"/logs", "/notificaciones/logs"})
    public ResponseEntity<Map<String, Object>> obtenerLogs() {
        List<NotificacionLog> logs = notificacionRepository.findAll();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensajeGlobal", mensajeGlobal);
        respuesta.put("canalConfigurado", canal);
        respuesta.put("modoSimuladoConfigurado", modoSimulado);
        respuesta.put("totalLogs", logs.size());
        respuesta.put("logs", logs);

        return ResponseEntity.ok(respuesta);
    }

    // 2. POST /notificacion (y /notificaciones) inyectando canal y modo-simulado
    @PostMapping({"/notificacion", "/notificaciones", "/notificaciones/notificacion"})
    public ResponseEntity<Map<String, Object>> enviarNotificacion(@RequestBody NotificacionLog request) {
        if (request.getFecha() == null) {
            request.setFecha(LocalDateTime.now());
        }

        // Simulación de envío según la configuración externa
        if (Boolean.TRUE.equals(modoSimulado)) {
            log.info(">>> [SIMULACIÓN ACTIVA] Enviando notificación a través del canal '{}' a '{}': {}",
                    canal, request.getDestinatario(), request.getMensaje());
        } else {
            log.info(">>> [ENVÍO REAL] Despachando notificación por canal '{}' a '{}': {}",
                    canal, request.getDestinatario(), request.getMensaje());
        }

        NotificacionLog guardado = notificacionRepository.save(request);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensajeGlobal", mensajeGlobal);
        respuesta.put("canalUtilizado", canal);
        respuesta.put("modoSimulado", modoSimulado);
        respuesta.put("estado", "NOTIFICACION_REGISTRADA");
        respuesta.put("detalle", guardado);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // 3. DELETE /{id} (y /notificaciones/{id})
    @DeleteMapping({"/{id}", "/notificaciones/{id}"})
    public ResponseEntity<?> eliminarLog(@PathVariable Long id) {
        if (!notificacionRepository.existsById(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Log de notificación con ID " + id + " no existe.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        notificacionRepository.deleteById(id);
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Log de notificación con ID " + id + " eliminado con éxito.");
        return ResponseEntity.ok(respuesta);
    }
}

