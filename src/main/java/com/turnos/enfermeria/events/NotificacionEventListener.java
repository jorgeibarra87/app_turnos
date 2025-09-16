// NotificacionEventListener.java
package com.turnos.enfermeria.events;

import com.turnos.enfermeria.service.NotificacionAutomaticaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class NotificacionEventListener {

    private final NotificacionAutomaticaService notificacionAutomaticaService;

    @EventListener
    @Async
    public void manejarCambioCuadro(CambioCuadroEvent event) {
        try {
            log.info("🎯 Evento recibido: {} para cuadro ID: {}",
                    event.getTipoOperacion(), event.getIdCuadroTurno());

            // Enviar notificación automática
            notificacionAutomaticaService.enviarNotificacionCambioCuadro(
                    event.getIdCuadroTurno(),
                    event.getTipoOperacion(),
                    event.getDetallesOperacion()
            );

        } catch (Exception e) {
            log.error("❌ Error procesando evento de cambio de cuadro: {}", e.getMessage(), e);
        }
    }

    // Manejar eventos de cambios en equipos
    @EventListener
    @Async
    public void manejarCambioEquipo(CambioEquipoEvent event) {
        try {
            log.info("🎯 Evento recibido: {} para equipo ID: {}",
                    event.getTipoOperacion(), event.getIdEquipo());

            notificacionAutomaticaService.enviarNotificacionCambioEquipo(
                    event.getIdEquipo(),
                    event.getTipoOperacion(),
                    event.getDetallesOperacion()
            );
        } catch (Exception e) {
            log.error("❌ Error procesando evento de cambio de equipo: {}", e.getMessage(), e);
        }
    }

    // Manejar eventos de cambios en asignación de personas
    @EventListener
    @Async
    public void manejarCambioPersonaEquipo(CambioPersonaEquipoEvent event) {
        try {
            log.info("🎯 Evento recibido: {} para persona ID: {} en equipo ID: {}",
                    event.getTipoOperacion(), event.getIdPersona(), event.getIdEquipo());

            notificacionAutomaticaService.enviarNotificacionCambioPersonaEquipo(
                    event.getIdPersona(),
                    event.getIdEquipo(),
                    event.getTipoOperacion(),
                    event.getDetallesOperacion()
            );
        } catch (Exception e) {
            log.error("❌ Error procesando evento de cambio persona-equipo: {}", e.getMessage(), e);
        }
    }
}
