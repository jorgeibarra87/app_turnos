package com.turnos.enfermeria.events;

import com.turnos.enfermeria.service.NotificacionAutomaticaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CambiosInterceptor {

    @Autowired
    private NotificacionAutomaticaService notificacionAutomaticaService;

    @EventListener
    public void manejarCambioCuadro(CambioCuadroEvent evento) {
        log.info("🔄 Detectado cambio en cuadro: {}", evento.getIdCuadroTurno());

        notificacionAutomaticaService.enviarNotificacionCambioCuadro(
                evento.getIdCuadroTurno(),
                evento.getTipoOperacion(),
                evento.getDetallesOperacion()
        );
    }

    @EventListener
    public void manejarCambioTurno(CambioTurnoEvent evento) {
        log.info("🔄 Detectado cambio en turno: {}", evento.getIdTurno());

        notificacionAutomaticaService.enviarNotificacionCambioTurno(
                evento.getIdTurno(),
                evento.getTipoOperacion(),
                evento.getDetallesOperacion()
        );
    }
}
