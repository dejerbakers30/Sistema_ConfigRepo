package com.tienda.msnotificaciones.repository;

import com.tienda.msnotificaciones.model.NotificacionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionLog, Long> {
}

