package com.tienda.msnotificaciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion_log")
public class NotificacionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String destinatario;

    @Column(nullable = false, length = 255)
    private String mensaje;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public NotificacionLog() {
    }

    public NotificacionLog(Long id, String destinatario, String mensaje, LocalDateTime fecha) {
        this.id = id;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public NotificacionLog(String destinatario, String mensaje, LocalDateTime fecha) {
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "NotificacionLog{" +
                "id=" + id +
                ", destinatario='" + destinatario + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}

