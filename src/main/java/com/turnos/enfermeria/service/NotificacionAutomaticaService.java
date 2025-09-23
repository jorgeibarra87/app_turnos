package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@Slf4j
@AllArgsConstructor
public class NotificacionAutomaticaService {

    private final NotificacionService notificacionService;
    private final ConfiguracionCorreosRepository configuracionCorreosRepository;
    private final CuadroTurnoRepository cuadroTurnoRepository;
    private final TurnosRepository turnosRepository;
    private final CambiosCuadroTurnoRepository cambiosCuadroTurnoRepository;
    private final CambiosTurnoRepository cambiosTurnoRepository;
    private final EquipoRepository equipoRepository;
    private final PersonaRepository personaRepository;
    private final ProcesosAtencionRepository procesosAtencionRepository;
    private final CambiosEquipoRepository cambiosEquipoRepository;
    private final CambiosPersonaEquipoRepository cambiosPersonaEquipoRepository;

    /**
     * Envía notificación automática cuando se realizan cambios en cuadro de turno
     */
    @Async
    @Transactional
    public void enviarNotificacionCambioCuadro(Long idCuadroTurno, String tipoOperacion, String detallesOperacion) {
        try {
            log.info("🔔 Iniciando notificación automática para cambio en cuadro: {}", idCuadroTurno);

            // 1. Obtener correos activos
            List<ConfiguracionCorreos> correosActivos = configuracionCorreosRepository.findByActivoTrue();

            if (correosActivos.isEmpty()) {
                log.warn("⚠️ No hay correos activos configurados para notificaciones");
                return;
            }

            // 2. Recopilar todos los datos del cuadro
            DatosNotificacionDTO datosNotificacion = recopilarDatosCuadro(idCuadroTurno);

            // 3. Generar HTML del correo
            String htmlContent = generarHTMLNotificacion(
                    datosNotificacion,
                    tipoOperacion,
                    detallesOperacion
            );

            // 4. Crear notificaciones para envío
            List<NotificacionDTO> notificaciones = correosActivos.stream()
                    .map(correo -> crearNotificacionDTO(
                            correo,
                            htmlContent,
                            tipoOperacion,
                            datosNotificacion.getCuadroData().getNombre(),
                            idCuadroTurno
                    ))
                    .collect(Collectors.toList());

            // 5. Enviar notificaciones
            notificacionService.enviarNotificacionesAutomaticas(notificaciones);

            log.info("✅ Notificaciones automáticas enviadas a {} destinatarios", correosActivos.size());

        } catch (Exception e) {
            log.error("❌ Error al enviar notificación automática para cuadro {}: {}", idCuadroTurno, e.getMessage(), e);
        }
    }

    /**
     * Envía notificación automática cuando se realizan cambios en turnos
     */
    @Async
    @Transactional
    public void enviarNotificacionCambioTurno(Long idTurno, String tipoOperacion, String detallesOperacion) {
        try {
            log.info("🔔 Iniciando notificación automática para cambio en turno: {}", idTurno);

            // Obtener el cuadro asociado al turno
            Turnos turno = turnosRepository.findById(idTurno)
                    .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado: " + idTurno));

            // Enviar notificación usando el cuadro asociado
            enviarNotificacionCambioCuadro(
                    turno.getCuadroTurno().getIdCuadroTurno(),
                    "CAMBIO EN TURNO: " + tipoOperacion,
                    detallesOperacion + " (Turno ID: " + idTurno + ")"
            );

        } catch (Exception e) {
            log.error("❌ Error al enviar notificación automática para turno {}: {}", idTurno, e.getMessage(), e);
        }
    }

    /**
     * Recopila todos los datos necesarios para la notificación
     */
    private DatosNotificacionDTO recopilarDatosCuadro(Long idCuadroTurno) {
        DatosNotificacionDTO datos = new DatosNotificacionDTO();

        try {
            // 1. Datos del cuadro
            CuadroTurno cuadro = cuadroTurnoRepository.findById(idCuadroTurno)
                    .orElseThrow(() -> new EntityNotFoundException("Cuadro no encontrado"));

            datos.setCuadroData(mapearCuadroADTO(cuadro));

            // 2. Miembros del equipo
            if (cuadro.getIdEquipo() != null) {
                List<MiembroPerfilDTO> miembros = obtenerMiembrosEquipo(cuadro.getIdEquipo());
                datos.setMiembros(miembros);
            }

            // 3. Turnos del cuadro
            List<TurnoDTO> turnos = obtenerTurnosCuadro(idCuadroTurno);
            datos.setTurnos(turnos);

            // 4. Historial de cambios del cuadro
            List<CambioCuadroDTO> historialCuadro = obtenerHistorialCambioCuadro(idCuadroTurno);
            datos.setHistorialCuadro(historialCuadro);

            // 5. Historial de cambios de turnos
            List<CambioTurnoDTO> historialTurnos = obtenerHistorialCambioTurnos(idCuadroTurno);
            datos.setHistorialTurnos(historialTurnos);

            // 6. Procesos (si es multiproceso)
            if ("multiproceso".equals(cuadro.getCategoria())) {
                List<ProcesoDTO> procesos = obtenerProcesos(idCuadroTurno);
                datos.setProcesos(procesos);
            }

            List<CambiosEquipoDTO> historialEquipos = obtenerHistorialEquipos(idCuadroTurno);
            datos.setHistorialEquipos(historialEquipos);

            List<CambiosPersonaEquipoDTO> historialPersonasEquipo = obtenerHistorialPersonasEquipo(idCuadroTurno);
            datos.setHistorialPersonasEquipo(historialPersonasEquipo);

        } catch (Exception e) {
            log.error("Error recopilando datos del cuadro {}: {}", idCuadroTurno, e.getMessage());
            throw new RuntimeException("Error recopilando datos para notificación", e);
        }

        return datos;
    }

    // Notificaciones para cambios en equipos
    @Async
    @Transactional
    public void enviarNotificacionCambioEquipo(Long idEquipo, String tipoOperacion, String detallesOperacion) {
        try {
            log.info("🔔 Iniciando notificación automática para cambio en equipo: {}", idEquipo);

            List<ConfiguracionCorreos> correosActivos = configuracionCorreosRepository.findByActivoTrue();

            if (correosActivos.isEmpty()) {
                log.warn("⚠️ No hay correos activos configurados para notificaciones");
                return;
            }

            // Recopilar datos del equipo
            DatosNotificacionEquipoDTO datosNotificacion = recopilarDatosEquipo(idEquipo);

            // Generar HTML del correo
            String htmlContent = generarHTMLNotificacionEquipo(
                    datosNotificacion,
                    tipoOperacion,
                    detallesOperacion
            );

            // Crear notificaciones para envío
            List<NotificacionDTO> notificaciones = correosActivos.stream()
                    .map(correo -> crearNotificacionEquipoDTO(
                            correo,
                            htmlContent,
                            tipoOperacion,
                            datosNotificacion.getEquipo().getNombre(),
                            idEquipo
                    ))
                    .collect(Collectors.toList());

            // Enviar notificaciones
            notificacionService.enviarNotificacionesAutomaticas(notificaciones);

            log.info("✅ Notificaciones automáticas de equipo enviadas a {} destinatarios", correosActivos.size());

        } catch (Exception e) {
            log.error("❌ Error al enviar notificación automática para equipo {}: {}", idEquipo, e.getMessage(), e);
        }
    }

    // Notificaciones para cambios en personas de equipos
    @Async
    @Transactional
    public void enviarNotificacionCambioPersonaEquipo(Long idPersona, Long idEquipo, String tipoOperacion, String detallesOperacion) {
        try {
            log.info("🔔 Iniciando notificación automática para cambio persona-equipo: persona={}, equipo={}", idPersona, idEquipo);

            List<ConfiguracionCorreos> correosActivos = configuracionCorreosRepository.findByActivoTrue();

            if (correosActivos.isEmpty()) {
                log.warn("⚠️ No hay correos activos configurados para notificaciones");
                return;
            }

            // Recopilar datos de la persona y equipo
            DatosNotificacionPersonaEquipoDTO datosNotificacion = recopilarDatosPersonaEquipo(idPersona, idEquipo);

            // Generar HTML del correo
            String htmlContent = generarHTMLNotificacionPersonaEquipo(
                    datosNotificacion,
                    tipoOperacion,
                    detallesOperacion
            );

            // Crear notificaciones para envío
            List<NotificacionDTO> notificaciones = correosActivos.stream()
                    .map(correo -> crearNotificacionPersonaEquipoDTO(
                            correo,
                            htmlContent,
                            tipoOperacion,
                            datosNotificacion.getPersona().getNombreCompleto(),
                            datosNotificacion.getEquipo().getNombre()
                    ))
                    .collect(Collectors.toList());

            // Enviar notificaciones
            notificacionService.enviarNotificacionesAutomaticas(notificaciones);

            log.info("✅ Notificaciones automáticas persona-equipo enviadas a {} destinatarios", correosActivos.size());

        } catch (Exception e) {
            log.error("❌ Error al enviar notificación automática persona-equipo: {}", e.getMessage(), e);
        }
    }

    /**
     * Genera el HTML completo para notificaciones de equipos
     */
    private String generarHTMLNotificacionEquipo(DatosNotificacionEquipoDTO datos, String tipoOperacion, String detallesOperacion) {
        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                .container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; }
                .header { background: #16a34a; color: white; padding: 20px; text-align: center; }
                .content { padding: 20px; }
                .section { margin-bottom: 30px; }
                .section-title { background: #f8fafc; padding: 10px; margin-bottom: 15px; border-left: 4px solid #16a34a; font-weight: bold; }
                .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 15px; margin-bottom: 20px; }
                .info-card { border: 1px solid #e5e7eb; border-radius: 6px; padding: 15px; }
                .info-label { font-size: 12px; color: #6b7280; margin-bottom: 5px; }
                .info-value { font-weight: 500; color: #1f2937; }
                table { width: 100%%; border-collapse: collapse; margin-top: 10px; }
                th { background: #1f2937; color: white; padding: 8px; text-align: left; font-size: 12px; }
                td { padding: 8px; border-bottom: 1px solid #e5e7eb; font-size: 12px; }
                tr:hover { background: #f9fafb; }
                .status-active { background: #dcfce7; color: #166534; padding: 2px 6px; border-radius: 12px; }
                .status-inactive { background: #fed7d7; color: #c53030; padding: 2px 6px; border-radius: 12px; }
                .alert { padding: 15px; border-radius: 6px; margin-bottom: 20px; }
                .alert-success { background: #d1fae5; border: 1px solid #16a34a; color: #065f46; }
                .footer { background: #f8fafc; padding: 15px; text-align: center; color: #6b7280; font-size: 12px; }
            </style>
        </head>
        <body>
            <div class="container">
                <!-- Header -->
                <div class="header">
                    <h1>🏥 Notificación de Cambio en Equipos de Trabajo</h1>
                    <p>Sistema de Gestión de Turnos Hospital Universitario San Jose - %s</p>
                </div>

                <div class="content">
                    <!-- Alerta de Operación -->
                    <div class="alert alert-success">
                        <strong>Tipo de Operación:</strong> %s<br>
                        <strong>Detalles:</strong> %s
                    </div>

                    %s <!-- Contenido dinámico del equipo -->
                </div>

                <div class="footer">
                    <p>Este correo ha sido generado automáticamente por el Sistema de Turnos (HUSJ)</p>
                    <p>Por favor, no responder a este correo</p>
                </div>
            </div>
        </body>
        </html>
        """,
                fechaActual,
                tipoOperacion,
                detallesOperacion,
                generarContenidoEquipo(datos)
        );
    }

    /**
     * Genera el HTML completo para notificaciones de persona-equipo
     */
    private String generarHTMLNotificacionPersonaEquipo(DatosNotificacionPersonaEquipoDTO datos, String tipoOperacion, String detallesOperacion) {
        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                .container { max-width: 1000px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; }
                .header { background: #7c3aed; color: white; padding: 20px; text-align: center; }
                .content { padding: 20px; }
                .section { margin-bottom: 30px; }
                .section-title { background: #f8fafc; padding: 10px; margin-bottom: 15px; border-left: 4px solid #7c3aed; font-weight: bold; }
                .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px; margin-bottom: 20px; }
                .info-card { border: 1px solid #e5e7eb; border-radius: 6px; padding: 15px; }
                .info-label { font-size: 12px; color: #6b7280; margin-bottom: 5px; }
                .info-value { font-weight: 500; color: #1f2937; }
                table { width: 100%%; border-collapse: collapse; margin-top: 10px; }
                th { background: #1f2937; color: white; padding: 8px; text-align: left; font-size: 12px; }
                td { padding: 8px; border-bottom: 1px solid #e5e7eb; font-size: 12px; }
                tr:hover { background: #f9fafb; }
                .status-active { background: #dcfce7; color: #166534; padding: 2px 6px; border-radius: 12px; }
                .status-inactive { background: #fed7d7; color: #c53030; padding: 2px 6px; border-radius: 12px; }
                .alert { padding: 15px; border-radius: 6px; margin-bottom: 20px; }
                .alert-info { background: #ede9fe; border: 1px solid #7c3aed; color: #5b21b6; }
                .footer { background: #f8fafc; padding: 15px; text-align: center; color: #6b7280; font-size: 12px; }
            </style>
        </head>
        <body>
            <div class="container">
                <!-- Header -->
                <div class="header">
                    <h1>👤 Notificación de Cambio en Asignación de Personal</h1>
                    <p>Sistema de Gestión Turnos Hospital Universitario San Jose - %s</p>
                </div>

                <div class="content">
                    <!-- Alerta de Operación -->
                    <div class="alert alert-info">
                        <strong>Tipo de Operación:</strong> %s<br>
                        <strong>Detalles:</strong> %s
                    </div>

                    %s <!-- Contenido dinámico de persona-equipo -->
                </div>

                <div class="footer">
                    <p>Este correo ha sido generado automáticamente por el Sistema de Turnos (HUSJ)</p>
                    <p>Por favor, no responder a este correo</p>
                </div>
            </div>
        </body>
        </html>
        """,
                fechaActual,
                tipoOperacion,
                detallesOperacion,
                generarContenidoPersonaEquipo(datos)
        );
    }

    /**
     * Genera el contenido específico del equipo para el HTML
     */
    private String generarContenidoEquipo(DatosNotificacionEquipoDTO datos) {
        StringBuilder contenido = new StringBuilder();

        // Información del equipo
        contenido.append(generarSeccionInformacionEquipo(datos.getEquipo()));

        // Miembros del equipo
        if (datos.getMiembros() != null && !datos.getMiembros().isEmpty()) {
            contenido.append(generarSeccionMiembrosEquipo(datos.getMiembros()));
        }

        // Historial de cambios del equipo
        if (datos.getHistorialEquipo() != null && !datos.getHistorialEquipo().isEmpty()) {
            contenido.append(generarSeccionHistorialEquipo(datos.getHistorialEquipo()));
        }

        // Historial de cambios de personas
        if (datos.getHistorialPersonas() != null && !datos.getHistorialPersonas().isEmpty()) {
            contenido.append(generarSeccionHistorialPersonas(datos.getHistorialPersonas()));
        }

        return contenido.toString();
    }

    /**
     * Genera el contenido específico de persona-equipo para el HTML
     */
    private String generarContenidoPersonaEquipo(DatosNotificacionPersonaEquipoDTO datos) {
        StringBuilder contenido = new StringBuilder();

        // Información de la persona
        contenido.append(generarSeccionInformacionPersona(datos.getPersona()));

        // Información del equipo actual
        contenido.append(generarSeccionInformacionEquipoActual(datos.getEquipo()));

        // Equipos anteriores (si aplica)
        if (datos.getEquiposAnteriores() != null && !datos.getEquiposAnteriores().isEmpty()) {
            contenido.append(generarSeccionEquiposAnteriores(datos.getEquiposAnteriores()));
        }

        // Historial de cambios de la persona
        if (datos.getHistorialCambios() != null && !datos.getHistorialCambios().isEmpty()) {
            contenido.append(generarSeccionHistorialPersona(datos.getHistorialCambios()));
        }

        return contenido.toString();
    }

    /**
     * ✅ MÉTODO FALTANTE: Genera la sección de equipos anteriores
     */
    private String generarSeccionEquiposAnteriores(List<EquipoDTO> equiposAnteriores) {
        StringBuilder tabla = new StringBuilder(String.format("""
        <div class="section">
            <div class="section-title">📋 Equipos Anteriores (%d equipos)</div>
            <table>
                <thead>
                    <tr>
                        <th>ID Equipo</th>
                        <th>Nombre del Equipo</th>
                        <th>Estado</th>
                    </tr>
                </thead>
                <tbody>
        """, equiposAnteriores.size()));

        for (EquipoDTO equipo : equiposAnteriores) {
            tabla.append(String.format("""
                    <tr>
                        <td>#%s</td>
                        <td>%s</td>
                        <td><span class="%s">%s</span></td>
                    </tr>
            """,
                    equipo.getIdEquipo() != null ? equipo.getIdEquipo() : "N/A",
                    equipo.getNombre() != null ? equipo.getNombre() : "No especificado",
                    equipo.getEstado() != null && equipo.getEstado() ? "status-active" : "status-inactive",
                    equipo.getEstado() != null && equipo.getEstado() ? "Activo" : "Inactivo"
            ));
        }

        tabla.append("""
                </tbody>
            </table>
        </div>
        """);

        return tabla.toString();
    }

    /**
     * ✅ MÉTODO FALTANTE: Genera la sección del historial de cambios de la persona
     */
    private String generarSeccionHistorialPersona(List<CambiosPersonaEquipoDTO> historialCambios) {
        StringBuilder tabla = new StringBuilder(String.format("""
        <div class="section">
            <div class="section-title">📊 Historial de Cambios de la Persona (Últimos %d)</div>
            <table>
                <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Tipo de Cambio</th>
                        <th>Equipo Anterior</th>
                        <th>Equipo Nuevo</th>
                        <th>Resumen del Cambio</th>
                        <th>Observaciones</th>
                    </tr>
                </thead>
                <tbody>
        """, historialCambios.size()));

        for (CambiosPersonaEquipoDTO cambio : historialCambios) {
            tabla.append(String.format("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                    </tr>
            """,
                    cambio.getFechaCambio() != null
                            ? cambio.getFechaCambio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            : "N/A",
                    cambio.getTipoCambio() != null ? cambio.getTipoCambio() : "N/A",
                    cambio.getNombreEquipoAnterior() != null ? cambio.getNombreEquipoAnterior() : "-",
                    cambio.getNombreEquipoNuevo() != null ? cambio.getNombreEquipoNuevo() : "-",
                    cambio.getResumenCambio() != null ? cambio.getResumenCambio() : "Sin resumen disponible",
                    cambio.getObservaciones() != null ? cambio.getObservaciones() : "Sin observaciones"
            ));
        }

        tabla.append("""
                </tbody>
            </table>
        </div>
        """);

        return tabla.toString();
    }


    /**
     * Genera la sección de información del equipo
     */
    private String generarSeccionInformacionEquipo(EquipoDTO equipo) {
        return String.format("""
        <div class="section">
            <div class="section-title">👥 Información del Equipo</div>
            <div class="info-grid">
                <div class="info-card">
                    <div class="info-label">Nombre del Equipo</div>
                    <div class="info-value">%s</div>
                </div>
                <div class="info-card">
                    <div class="info-label">ID del Equipo</div>
                    <div class="info-value">#%s</div>
                </div>
                <div class="info-card">
                    <div class="info-label">Estado</div>
                    <div class="info-value">
                        <span class="%s">%s</span>
                    </div>
                </div>
                <div class="info-card">
                    <div class="info-label">Observaciones</div>
                    <div class="info-value">%s</div>
                </div>
                <div class="info-card">
                    <div class="info-label">Fecha de Creación</div>
                    <div class="info-value">%s</div>
                </div>
            </div>
        </div>
        """,
                equipo.getNombre() != null ? equipo.getNombre() : "No especificado",
                equipo.getIdEquipo() != null ? equipo.getIdEquipo() : "N/A",
                equipo.getEstado() != null && equipo.getEstado() ? "status-active" : "status-inactive",
                equipo.getEstado() != null && equipo.getEstado() ? "Activo" : "Inactivo",
                equipo.getObservaciones() != null ? equipo.getObservaciones() : "Sin observaciones",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    /**
     * Genera la sección de miembros del equipo
     */
    private String generarSeccionMiembrosEquipo(List<MiembroPerfilDTO> miembros) {
        StringBuilder tabla = new StringBuilder("""
        <div class="section">
            <div class="section-title">👤 Miembros del Equipo (%d personas)</div>
            <table>
                <thead>
                    <tr>
                        <th>Nombre Completo</th>
                        <th>Documento</th>
                        <th>Perfiles</th>
                    </tr>
                </thead>
                <tbody>
        """.formatted(miembros.size()));

        for (MiembroPerfilDTO miembro : miembros) {
            tabla.append(String.format("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                    </tr>
            """,
                    miembro.getNombreCompleto() != null ? miembro.getNombreCompleto() : "Nombre no disponible",
                    miembro.getDocumento() != null ? miembro.getDocumento() : "N/A",
                    miembro.getTitulos() != null && !miembro.getTitulos().isEmpty()
                            ? String.join(", ", miembro.getTitulos())
                            : "Sin perfil definido"
            ));
        }

        tabla.append("""
                </tbody>
            </table>
        </div>
        """);

        return tabla.toString();
    }

    /**
     * Genera la sección del historial de cambios del equipo
     */
    private String generarSeccionHistorialEquipo(List<CambiosEquipoDTO> historial) {
        StringBuilder tabla = new StringBuilder("""
        <div class="section">
            <div class="section-title">📊 Historial de Cambios del Equipo (Últimos %d)</div>
            <table>
                <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Tipo de Cambio</th>
                        <th>Nombre Anterior</th>
                        <th>Nombre Nuevo</th>
                        <th>Estado</th>
                        <th>Observaciones</th>
                    </tr>
                </thead>
                <tbody>
        """.formatted(historial.size()));

        for (CambiosEquipoDTO cambio : historial) {
            tabla.append(String.format("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td><span class="%s">%s</span></td>
                        <td>%s</td>
                    </tr>
            """,
                    cambio.getFechaCambio() != null
                            ? cambio.getFechaCambio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            : "N/A",
                    cambio.getTipoCambio() != null ? cambio.getTipoCambio() : "N/A",
                    cambio.getNombreAnterior() != null ? cambio.getNombreAnterior() : "-",
                    cambio.getNombreNuevo() != null ? cambio.getNombreNuevo() : "-",
                    cambio.getEstadoNuevo() != null && cambio.getEstadoNuevo() ? "status-active" : "status-inactive",
                    cambio.getEstadoNuevo() != null && cambio.getEstadoNuevo() ? "Activo" : "Inactivo",
                    cambio.getObservaciones() != null ? cambio.getObservaciones() : "Sin observaciones"
            ));
        }

        tabla.append("""
                </tbody>
            </table>
        </div>
        """);

        return tabla.toString();
    }

    /**
     * Genera la sección del historial de personas en equipos
     */
    private String generarSeccionHistorialPersonas(List<CambiosPersonaEquipoDTO> historial) {
        StringBuilder tabla = new StringBuilder("""
        <div class="section">
            <div class="section-title">🔄 Historial de Cambios de Personal (Últimos %d)</div>
            <table>
                <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Tipo de Cambio</th>
                        <th>Persona</th>
                        <th>Equipo Anterior</th>
                        <th>Equipo Nuevo</th>
                        <th>Observaciones</th>
                    </tr>
                </thead>
                <tbody>
        """.formatted(historial.size()));

        for (CambiosPersonaEquipoDTO cambio : historial) {
            tabla.append(String.format("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                    </tr>
            """,
                    cambio.getFechaCambio() != null
                            ? cambio.getFechaCambio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            : "N/A",
                    cambio.getTipoCambio() != null ? cambio.getTipoCambio() : "N/A",
                    cambio.getNombrePersona() != null ? cambio.getNombrePersona() : "Persona ID: " + cambio.getIdPersona(),
                    cambio.getNombreEquipoAnterior() != null ? cambio.getNombreEquipoAnterior() : "-",
                    cambio.getNombreEquipoNuevo() != null ? cambio.getNombreEquipoNuevo() : "-",
                    cambio.getObservaciones() != null ? cambio.getObservaciones() : "Sin observaciones"
            ));
        }

        tabla.append("""
                </tbody>
            </table>
        </div>
        """);

        return tabla.toString();
    }

    /**
     * Genera la sección de información de la persona
     */
    private String generarSeccionInformacionPersona(PersonaDTO persona) {
        return String.format("""
        <div class="section">
            <div class="section-title">👤 Información de la Persona</div>
            <div class="info-grid">
                <div class="info-card">
                    <div class="info-label">Nombre Completo</div>
                    <div class="info-value">%s</div>
                </div>
                <div class="info-card">
                    <div class="info-label">Documento</div>
                    <div class="info-value">%s</div>
                </div>
                <div class="info-card">
                    <div class="info-label">ID de Persona</div>
                    <div class="info-value">#%s</div>
                </div>
                <div class="info-card">
                    <div class="info-label">Estado</div>
                    <div class="info-value">
                        <span class="status-active">Activo</span>
                    </div>
                </div>
            </div>
        </div>
        """,
                persona.getNombreCompleto() != null ? persona.getNombreCompleto() : "No especificado",
                persona.getDocumento() != null ? persona.getDocumento() : "N/A",
                persona.getIdPersona() != null ? persona.getIdPersona() : "N/A"
        );
    }

    /**
     * Genera la sección de información del equipo actual
     */
    private String generarSeccionInformacionEquipoActual(EquipoDTO equipo) {
        return String.format("""
        <div class="section">
            <div class="section-title">👥 Equipo Actual</div>
            <div class="info-grid">
                <div class="info-card">
                    <div class="info-label">Nombre del Equipo</div>
                    <div class="info-value">%s</div>
                </div>
                <div class="info-card">
                    <div class="info-label">ID del Equipo</div>
                    <div class="info-value">#%s</div>
                </div>
                <div class="info-card">
                    <div class="info-label">Estado del Equipo</div>
                    <div class="info-value">
                        <span class="%s">%s</span>
                    </div>
                </div>
                <div class="info-card">
                    <div class="info-label">Observaciones</div>
                    <div class="info-value">%s</div>
                </div>
            </div>
        </div>
        """,
                equipo.getNombre() != null ? equipo.getNombre() : "No especificado",
                equipo.getIdEquipo() != null ? equipo.getIdEquipo() : "N/A",
                equipo.getEstado() != null && equipo.getEstado() ? "status-active" : "status-inactive",
                equipo.getEstado() != null && equipo.getEstado() ? "Activo" : "Inactivo",
                equipo.getObservaciones() != null ? equipo.getObservaciones() : "Sin observaciones"
        );
    }

    /**
     * Métodos auxiliares para crear DTOs de notificación
     */
    private NotificacionDTO crearNotificacionEquipoDTO(ConfiguracionCorreos correo, String htmlContent,
                                                       String tipoOperacion, String nombreEquipo, Long idEquipo) {
        NotificacionDTO notificacion = new NotificacionDTO();
        notificacion.setCorreo(correo.getCorreo());
        notificacion.setAsunto("👥 " + tipoOperacion + " - " + nombreEquipo);
        notificacion.setMensaje(htmlContent);
        notificacion.setEstado(true);
        notificacion.setPermanente(correo.getTipoCorreo() == TipoCorreo.PERMANENTE);
        notificacion.setAutomatico(true);
        // Nota: Podrías agregar un campo idEquipo si lo necesitas en tu DTO
        return notificacion;
    }

    private NotificacionDTO crearNotificacionPersonaEquipoDTO(ConfiguracionCorreos correo, String htmlContent,
                                                              String tipoOperacion, String nombrePersona, String nombreEquipo) {
        NotificacionDTO notificacion = new NotificacionDTO();
        notificacion.setCorreo(correo.getCorreo());
        notificacion.setAsunto("👤 " + tipoOperacion + " - " + nombrePersona + " ↔ " + nombreEquipo);
        notificacion.setMensaje(htmlContent);
        notificacion.setEstado(true);
        notificacion.setPermanente(correo.getTipoCorreo() == TipoCorreo.PERMANENTE);
        notificacion.setAutomatico(true);
        return notificacion;
    }

    // ===== MÉTODOS AUXILIARES PARA OBTENER DATOS =====

    private CuadroTurnoDTO mapearCuadroADTO(CuadroTurno cuadro) {
        CuadroTurnoDTO dto = new CuadroTurnoDTO();
        dto.setIdCuadroTurno(cuadro.getIdCuadroTurno());
        dto.setNombre(cuadro.getNombre());
        dto.setMes(cuadro.getMes());
        dto.setAnio(cuadro.getAnio());
        dto.setVersion(cuadro.getVersion());
        dto.setCategoria(cuadro.getCategoria());
        dto.setEstadoCuadro(cuadro.getEstadoCuadro());
        dto.setTurnoExcepcion(cuadro.getTurnoExcepcion());
        dto.setIdEquipo(cuadro.getIdEquipo());
        dto.setObservaciones(cuadro.getObservaciones());

        // Nombres de entidades relacionadas (si están disponibles)
        if (cuadro.getMacroProcesos() != null) {
            dto.setNombreMacroproceso(cuadro.getNombreMacroproceso());
        }
        if (cuadro.getProcesos() != null) {
            dto.setNombreProceso(cuadro.getProcesos().getNombre());
        }
        if (cuadro.getServicios() != null) {
            dto.setNombreServicio(cuadro.getServicios().getNombre());
        }
        if (cuadro.getSeccionesServicios() != null) {
            dto.setNombreSeccionServicio(cuadro.getSeccionesServicios().getNombre());
        }
        if (cuadro.getSubseccionesServicios() != null) {
            dto.setNombreSubseccionServicio(cuadro.getSubseccionesServicios().getNombre());
        }

        return dto;
    }

    private List<MiembroPerfilDTO> obtenerMiembrosEquipo(Long equipoId) {
        try {
            // Usar el método del EquipoRepository que ya tienes
            List<Object[]> resultados = equipoRepository.findMiembrosConPerfilRaw(equipoId);

            return resultados.stream()
                    .map(fila -> {
                        MiembroPerfilDTO miembro = new MiembroPerfilDTO();
                        miembro.setIdPersona(((Number) fila[0]).longValue());
                        miembro.setNombreCompleto((String) fila[1]);
                        // Agregar más campos según tu implementación
                        return miembro;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error obteniendo miembros del equipo {}: {}", equipoId, e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<TurnoDTO> obtenerTurnosCuadro(Long idCuadroTurno) {
        try {
            List<Turnos> turnos = turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno);

            return turnos.stream()
                    .limit(10) // Limitar a 10 turnos más recientes
                    .map(this::mapearTurnoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error obteniendo turnos del cuadro {}: {}", idCuadroTurno, e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<CambiosEquipoDTO> obtenerHistorialEquipos(Long idCuadroTurno) {
        try {
            // Obtener cambios recientes de equipos
            List<CambiosEquipo> cambios = cambiosEquipoRepository.findAllByOrderByFechaCambioDesc();

            return cambios.stream()
                    .limit(15) // Últimos 15 cambios de equipos
                    .map(this::mapearCambioEquipoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error obteniendo historial de equipos: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    private List<CambiosEquipoDTO> obtenerHistorialCambiosEquipo(Long idEquipo) {
        try {
            List<CambiosEquipo> cambios = cambiosEquipoRepository
                    .findByEquipoIdEquipoOrderByFechaCambioDesc(idEquipo);
            return cambios.stream()
                    .limit(15) // Últimos 15 cambios del equipo
                    .map(this::mapearCambioEquipoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error obteniendo historial de cambios del equipo {}: {}", idEquipo, e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<CambiosPersonaEquipoDTO> obtenerHistorialPersonasEquipo(Long idEquipo) {
        try {
            List<CambiosPersonaEquipo> cambios = cambiosPersonaEquipoRepository
                    .findByEquipoIdEquipoOrderByFechaCambioDesc(idEquipo);
            return cambios.stream()
                    .limit(20) // Últimos 20 cambios de personas en este equipo
                    .map(this::mapearCambioPersonaEquipoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error obteniendo historial de personas del equipo {}: {}", idEquipo, e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<EquipoDTO> obtenerEquiposAnterioresPersona(Long idPersona) {
        try {
            // Obtener los últimos 5 equipos donde estuvo esta persona
            List<CambiosPersonaEquipo> cambiosPersona = cambiosPersonaEquipoRepository
                    .findByPersonaIdPersonaAndTipoCambioOrderByFechaCambioDesc(idPersona, "DESVINCULACION");

            return cambiosPersona.stream()
                    .limit(5)
                    .map(cambio -> cambio.getEquipoAnterior())
                    .filter(equipo -> equipo != null)
                    .distinct()
                    .map(this::mapearEquipoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error obteniendo equipos anteriores de la persona {}: {}", idPersona, e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<CambiosPersonaEquipoDTO> obtenerHistorialCambiosPersonaEspecifica(Long idPersona) {
        try {
            List<CambiosPersonaEquipo> cambios = cambiosPersonaEquipoRepository
                    .findByPersonaIdPersonaOrderByFechaCambioDesc(idPersona);
            return cambios.stream()
                    .limit(15) // Últimos 15 cambios de esta persona específica
                    .map(this::mapearCambioPersonaEquipoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error obteniendo historial de cambios de la persona {}: {}", idPersona, e.getMessage());
            return new ArrayList<>();
        }
    }

    private EquipoDTO mapearEquipoADTO(Equipo equipo) {
        EquipoDTO dto = new EquipoDTO();
        dto.setIdEquipo(equipo.getIdEquipo());
        dto.setNombre(equipo.getNombre());
        dto.setEstado(equipo.getEstado());
        dto.setObservaciones(equipo.getObservaciones());
        return dto;
    }

    private PersonaDTO mapearPersonaADTO(Persona persona) {
        PersonaDTO dto = new PersonaDTO();
        dto.setIdPersona(persona.getIdPersona());
        dto.setNombreCompleto(persona.getNombreCompleto());
        dto.setDocumento(persona.getDocumento());
        return dto;
    }

    // Métodos auxiliares para recopilar datos y generar HTML
    private DatosNotificacionEquipoDTO recopilarDatosEquipo(Long idEquipo) {
        DatosNotificacionEquipoDTO datos = new DatosNotificacionEquipoDTO();

        try {
            log.info("Recopilando datos del equipo ID: {}", idEquipo);

            // 1. Datos básicos del equipo
            Equipo equipo = equipoRepository.findById(idEquipo)
                    .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado"));
            datos.setEquipo(mapearEquipoADTO(equipo));

            // 2. Miembros actuales del equipo
            List<MiembroPerfilDTO> miembros = obtenerMiembrosEquipo(idEquipo);
            datos.setMiembros(miembros);

            // 3. Historial de cambios del equipo (últimos 15)
            List<CambiosEquipoDTO> historialEquipo = obtenerHistorialCambiosEquipo(idEquipo);
            datos.setHistorialEquipo(historialEquipo);

            // 4. Historial de cambios de personas en este equipo (últimos 20)
            List<CambiosPersonaEquipoDTO> historialPersonas = obtenerHistorialPersonasEquipo(idEquipo);
            datos.setHistorialPersonas(historialPersonas);

            log.info("Datos del equipo recopilados exitosamente. Miembros: {}, Historial equipo: {}, Historial personas: {}",
                    miembros.size(), historialEquipo.size(), historialPersonas.size());

        } catch (Exception e) {
            log.error("Error recopilando datos del equipo {}: {}", idEquipo, e.getMessage());
            throw new RuntimeException("Error recopilando datos para notificación de equipo", e);
        }

        return datos;
    }

    private DatosNotificacionPersonaEquipoDTO recopilarDatosPersonaEquipo(Long idPersona, Long idEquipo) {
        DatosNotificacionPersonaEquipoDTO datos = new DatosNotificacionPersonaEquipoDTO();

        try {
            log.info("Recopilando datos persona-equipo. Persona ID: {}, Equipo ID: {}", idPersona, idEquipo);

            // 1. Datos de la persona
            Persona persona = personaRepository.findById(idPersona)
                    .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));
            datos.setPersona(mapearPersonaADTO(persona));

            // 2. Datos del equipo actual
            Equipo equipo = equipoRepository.findById(idEquipo)
                    .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado"));
            datos.setEquipo(mapearEquipoADTO(equipo));

            // 3. Equipos anteriores de esta persona (últimos 5)
            List<EquipoDTO> equiposAnteriores = obtenerEquiposAnterioresPersona(idPersona);
            datos.setEquiposAnteriores(equiposAnteriores);

            // 4. Historial de cambios específicos de esta persona en equipos (últimos 15)
            List<CambiosPersonaEquipoDTO> historialCambios = obtenerHistorialCambiosPersonaEspecifica(idPersona);
            datos.setHistorialCambios(historialCambios);

            log.info("Datos persona-equipo recopilados exitosamente. Equipos anteriores: {}, Historial cambios: {}",
                    equiposAnteriores.size(), historialCambios.size());

        } catch (Exception e) {
            log.error("Error recopilando datos persona-equipo. Persona: {}, Equipo: {}: {}",
                    idPersona, idEquipo, e.getMessage());
            throw new RuntimeException("Error recopilando datos para notificación persona-equipo", e);
        }

        return datos;
    }

    private TurnoDTO mapearTurnoADTO(Turnos turno) {
        TurnoDTO dto = new TurnoDTO();
        dto.setIdTurno(turno.getIdTurno());
        dto.setFechaInicio(turno.getFechaInicio());
        dto.setFechaFin(turno.getFechaFin());
        dto.setTotalHoras(turno.getTotalHoras());
        dto.setTipoTurno(turno.getTipoTurno());
        dto.setEstadoTurno(turno.getEstadoTurno());
        dto.setJornada(turno.getJornada());
        dto.setVersion(turno.getVersion());
        dto.setComentarios(turno.getComentarios());
        dto.setIdCuadroTurno(turno.getCuadroTurno().getIdCuadroTurno());
        dto.setIdPersona(turno.getIdPersona());
        return dto;
    }

    private CambiosEquipoDTO mapearCambioEquipoADTO(CambiosEquipo cambio) {
        CambiosEquipoDTO dto = new CambiosEquipoDTO();
        dto.setIdCambioEquipo(cambio.getIdCambioEquipo());
        dto.setIdEquipo(cambio.getEquipo().getIdEquipo());
        dto.setFechaCambio(cambio.getFechaCambio());
        dto.setTipoCambio(cambio.getTipoCambio());
        dto.setNombreAnterior(cambio.getNombreAnterior());
        dto.setNombreNuevo(cambio.getNombreNuevo());
        dto.setEstadoAnterior(cambio.getEstadoAnterior());
        dto.setEstadoNuevo(cambio.getEstadoNuevo());
        dto.setObservaciones(cambio.getObservaciones());
        dto.setUsuarioCambio(cambio.getUsuarioCambio());

        if (cambio.getEquipo() != null) {
            dto.setNombreEquipo(cambio.getEquipo().getNombre());
            dto.setEstadoActual(cambio.getEquipo().getEstado());
        }

        return dto;
    }

    private CambiosPersonaEquipoDTO mapearCambioPersonaEquipoADTO(CambiosPersonaEquipo cambio) {
        CambiosPersonaEquipoDTO dto = new CambiosPersonaEquipoDTO();
        dto.setIdCambioPersonaEquipo(cambio.getIdCambioPersonaEquipo());
        dto.setIdPersona(cambio.getPersona().getIdPersona());
        dto.setIdEquipo(cambio.getEquipo().getIdEquipo());
        dto.setFechaCambio(cambio.getFechaCambio());
        dto.setTipoCambio(cambio.getTipoCambio());
        dto.setObservaciones(cambio.getObservaciones());
        dto.setUsuarioCambio(cambio.getUsuarioCambio());

        if (cambio.getPersona() != null) {
            dto.setNombrePersona(cambio.getPersona().getNombreCompleto());
            dto.setDocumentoPersona(cambio.getPersona().getDocumento());
        }

        if (cambio.getEquipo() != null) {
            dto.setNombreEquipo(cambio.getEquipo().getNombre());
        }

        if (cambio.getEquipoAnterior() != null) {
            dto.setEquipoAnteriorId(cambio.getEquipoAnterior().getIdEquipo());
            dto.setNombreEquipoAnterior(cambio.getEquipoAnterior().getNombre());
        }

        if (cambio.getEquipoNuevo() != null) {
            dto.setEquipoNuevoId(cambio.getEquipoNuevo().getIdEquipo());
            dto.setNombreEquipoNuevo(cambio.getEquipoNuevo().getNombre());
        }

        return dto;
    }

    /**
     * Obtiene historial de cambios del cuadro de turno
     */
    private List<CambioCuadroDTO> obtenerHistorialCambioCuadro(Long idCuadroTurno) {
        try {
            List<CambiosCuadroTurno> cambios = cambiosCuadroTurnoRepository
                    .findByCuadroTurno_IdCuadroTurnoOrderByFechaCambioDesc(idCuadroTurno);

            return cambios.stream()
                    .limit(10) // Últimos 10 cambios
                    .map(this::mapearCambioCuadroADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error obteniendo historial de cambios del cuadro {}: {}", idCuadroTurno, e.getMessage());
            return new ArrayList<>();
        }
    }

    private CambioCuadroDTO mapearCambioCuadroADTO(CambiosCuadroTurno cambio) {
        CambioCuadroDTO dto = new CambioCuadroDTO();
        dto.setIdCambioCuadro(cambio.getIdCambioCuadro());
        dto.setIdCuadroTurno(cambio.getCuadroTurno().getIdCuadroTurno());
        dto.setNombre(cambio.getNombre());
        dto.setMes(cambio.getMes());
        dto.setAnio(cambio.getAnio());
        dto.setEstadoCuadro(cambio.getEstadoCuadro());
        dto.setVersion(cambio.getVersion());
        dto.setTurnoExcepcion(cambio.getTurnoExcepcion());
        dto.setCategoria(cambio.getCategoria());
        dto.setEstado(cambio.getEstado());
        dto.setFechaCambio(cambio.getFechaCambio());

        // Nombres de entidades relacionadas
        if (cambio.getMacroProcesos() != null) {
            dto.setNombreMacroproceso(cambio.getMacroProcesos().getNombre());
        }
        if (cambio.getProcesos() != null) {
            dto.setNombreProceso(cambio.getProcesos().getNombre());
        }
        if (cambio.getServicios() != null) {
            dto.setNombreServicio(cambio.getServicios().getNombre());
        }

        return dto;
    }

    /**
     * Obtiene historial de cambios de turnos
     */
    private List<CambioTurnoDTO> obtenerHistorialCambioTurnos(Long idCuadroTurno) {
        try {
            List<CambiosTurno> cambios = cambiosTurnoRepository
                    .findByCuadroTurno_IdCuadroTurnoOrderByFechaCambioDesc(idCuadroTurno);

            return cambios.stream()
                    .limit(20) // Últimos 20 cambios de turnos
                    .map(this::mapearCambioTurnoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error obteniendo historial de cambios de turnos del cuadro {}: {}", idCuadroTurno, e.getMessage());
            return new ArrayList<>();
        }
    }

    private CambioTurnoDTO mapearCambioTurnoADTO(CambiosTurno cambio) {
        CambioTurnoDTO dto = new CambioTurnoDTO();
        dto.setIdCambio(cambio.getIdCambio());
        dto.setIdTurno(cambio.getTurno().getIdTurno());
        dto.setIdCuadroTurno(cambio.getCuadroTurno().getIdCuadroTurno());
        dto.setIdUsuario(cambio.getUsuario().getIdPersona());
        dto.setFechaCambio(cambio.getFechaCambio());
        dto.setFechaInicio(cambio.getFechaInicio());
        dto.setFechaFin(cambio.getFechaFin());
        dto.setTotalHoras(cambio.getTotalHoras());
        dto.setTipoTurno(cambio.getTipoTurno());
        dto.setEstadoTurno(cambio.getEstadoTurno());
        dto.setJornada(cambio.getJornada());
        dto.setVersion(cambio.getVersion());
        dto.setComentarios(cambio.getComentarios());

        return dto;
    }

    private List<ProcesoDTO> obtenerProcesos(Long idCuadroTurno) {
        try {
            List<ProcesosAtencion> procesos = procesosAtencionRepository
                    .findByCuadroTurno_IdCuadroTurno(idCuadroTurno);

            return procesos.stream()
                    .map(proceso -> {
                        ProcesoDTO dto = new ProcesoDTO();
                        dto.setIdProceso(proceso.getProcesos().getIdProceso());
                        dto.setNombre(proceso.getDetalle());
                        dto.setDetalle(proceso.getDetalle());
                        dto.setEstado(proceso.getEstado());
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error obteniendo procesos del cuadro {}: {}", idCuadroTurno, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Crea un DTO de notificación para envío
     */
    private NotificacionDTO crearNotificacionDTO(ConfiguracionCorreos correo, String htmlContent,
                                                 String tipoOperacion, String nombreCuadro, Long idCuadroTurno) {
        NotificacionDTO notificacion = new NotificacionDTO();
        notificacion.setCorreo(correo.getCorreo());
        notificacion.setAsunto("🏥 " + tipoOperacion + " - " + nombreCuadro);
        notificacion.setMensaje(htmlContent);
        notificacion.setEstado(true);
        notificacion.setPermanente(correo.getTipoCorreo() == TipoCorreo.PERMANENTE);
        notificacion.setAutomatico(true);
        notificacion.setIdCuadroTurno(idCuadroTurno);
        return notificacion;
    }

    /**
     * Genera el HTML del correo de notificación
     */
    private String generarHTMLNotificacion(DatosNotificacionDTO datos, String tipoOperacion, String detallesOperacion) {
        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                    .container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; }
                    .header { background: #2563eb; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .section { margin-bottom: 30px; }
                    .section-title { background: #f8fafc; padding: 10px; margin-bottom: 15px; border-left: 4px solid #2563eb; font-weight: bold; }
                    .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 15px; margin-bottom: 20px; }
                    .info-card { border: 1px solid #e5e7eb; border-radius: 6px; padding: 15px; }
                    .info-label { font-size: 12px; color: #6b7280; margin-bottom: 5px; }
                    .info-value { font-weight: 500; color: #1f2937; }
                    table { width: 100%%; border-collapse: collapse; margin-top: 10px; }
                    th { background: #1f2937; color: white; padding: 8px; text-align: left; font-size: 12px; }
                    td { padding: 8px; border-bottom: 1px solid #e5e7eb; font-size: 12px; }
                    tr:hover { background: #f9fafb; }
                    .status-active { background: #dcfce7; color: #166534; padding: 2px 6px; border-radius: 12px; }
                    .status-inactive { background: #fed7d7; color: #c53030; padding: 2px 6px; border-radius: 12px; }
                    .alert { padding: 15px; border-radius: 6px; margin-bottom: 20px; }
                    .alert-info { background: #dbeafe; border: 1px solid #3b82f6; color: #1e40af; }
                    .footer { background: #f8fafc; padding: 15px; text-align: center; color: #6b7280; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <!-- Header -->
                    <div class="header">
                        <h1>🏥 Notificación Automática de Cambio en Sistema de Turnos</h1>
                        <p>App Turnos Hospital Universitario San Jose - %s</p>
                    </div>

                    <div class="content">
                        <!-- Alerta de Operación -->
                        <div class="alert alert-info">
                            <strong>Tipo de Operación:</strong> %s<br>
                            <strong>Detalles:</strong> %s
                        </div>

                        %s <!-- Contenido dinámico del cuadro -->
                    </div>

                    <div class="footer">
                        <p>Este correo ha sido generado automáticamente por el Sistema de Turnos (HUSJ)</p>
                        <p>Por favor, no responder a este correo</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                fechaActual,
                tipoOperacion,
                detallesOperacion,
                generarContenidoCuadro(datos)
        );
    }

    /**
     * Genera el contenido específico del cuadro para el HTML
     */
    private String generarContenidoCuadro(DatosNotificacionDTO datos) {
        StringBuilder contenido = new StringBuilder();

        // Información del cuadro
        contenido.append(generarSeccionInformacionCuadro(datos.getCuadroData()));

        // Procesos (si aplica)
        if (datos.getProcesos() != null && !datos.getProcesos().isEmpty()) {
            contenido.append(generarSeccionProcesos(datos.getProcesos()));
        }

        // Equipo de trabajo
        if (datos.getMiembros() != null && !datos.getMiembros().isEmpty()) {
            contenido.append(generarSeccionEquipo(datos.getMiembros()));
        }

        // Turnos actuales
        if (datos.getTurnos() != null && !datos.getTurnos().isEmpty()) {
            contenido.append(generarSeccionTurnos(datos.getTurnos()));
        }

        // Historial de cambios del cuadro
        if (datos.getHistorialCuadro() != null && !datos.getHistorialCuadro().isEmpty()) {
            contenido.append(generarSeccionHistorialCuadro(datos.getHistorialCuadro()));
        }

        // Historial de cambios de turnos
        if (datos.getHistorialTurnos() != null && !datos.getHistorialTurnos().isEmpty()) {
            contenido.append(generarSeccionHistorialTurnos(datos.getHistorialTurnos()));
        }

        if (datos.getHistorialEquipos() != null && !datos.getHistorialEquipos().isEmpty()) {
            contenido.append(generarSeccionHistorialEquipos(datos.getHistorialEquipos()));
        }
        if (datos.getHistorialPersonasEquipo() != null && !datos.getHistorialPersonasEquipo().isEmpty()) {
            contenido.append(generarSeccionHistorialPersonasEquipo(datos.getHistorialPersonasEquipo()));
        }

        return contenido.toString();
    }

    // ===== MÉTODOS PARA GENERAR SECCIONES HTML =====

    private String generarSeccionInformacionCuadro(CuadroTurnoDTO cuadro) {
        return String.format("""
            <div class="section">
                <div class="section-title">📋 Información del Cuadro</div>
                <div class="info-grid">
                    <div class="info-card">
                        <div class="info-label">Nombre del Cuadro</div>
                        <div class="info-value">%s</div>
                    </div>
                    <div class="info-card">
                        <div class="info-label">Período</div>
                        <div class="info-value">%s/%s</div>
                    </div>
                    <div class="info-card">
                        <div class="info-label">Versión</div>
                        <div class="info-value">%s</div>
                    </div>
                    <div class="info-card">
                        <div class="info-label">Categoría</div>
                        <div class="info-value">%s</div>
                    </div>
                    <div class="info-card">
                        <div class="info-label">Estado</div>
                        <div class="info-value %s">%s</div>
                    </div>
                    <div class="info-card">
                        <div class="info-label">Turno Excepción</div>
                        <div class="info-value">%s</div>
                    </div>
                    <div class="info-card">
                         <div class="info-label">Observaciones</div>
                         <div class="info-value">%s</div>
                    </div>
                </div>
            </div>
            """,
                cuadro.getNombre(),
                cuadro.getMes(),
                cuadro.getAnio(),
                cuadro.getVersion(),
                cuadro.getCategoria(),
                cuadro.getEstadoCuadro() != null && cuadro.getEstadoCuadro().equals("abierto") ? "status-active" : "status-inactive",
                cuadro.getEstadoCuadro(),
                cuadro.getTurnoExcepcion() != null && cuadro.getTurnoExcepcion() ? "Sí" : "No",
                cuadro.getObservaciones() != null ? cuadro.getObservaciones() : "Sin observaciones"
        );
    }

    private String generarSeccionProcesos(List<ProcesoDTO> procesos) {
        StringBuilder tabla = new StringBuilder("""
            <div class="section">
                <div class="section-title">🔧 Procesos de Atención</div>
                <table>
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Detalle</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
            """);

        for (ProcesoDTO proceso : procesos) {
            tabla.append(String.format("""
                        <tr>
                            <td>%s</td>
                            <td>%s</td>
                            <td><span class="%s">%s</span></td>
                        </tr>
                """,
                    proceso.getNombre(),
                    proceso.getDetalle(),
                    proceso.getEstado() ? "status-active" : "status-inactive",
                    proceso.getEstado() ? "Activo" : "Inactivo"
            ));
        }

        tabla.append("""
                    </tbody>
                </table>
            </div>
            """);

        return tabla.toString();
    }

    private String generarSeccionEquipo(List<MiembroPerfilDTO> miembros) {
        StringBuilder tabla = new StringBuilder("""
            <div class="section">
                <div class="section-title">👥 Equipo de Trabajo</div>
                <table>
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Documento</th>
                            <th>Perfiles</th>
                        </tr>
                    </thead>
                    <tbody>
            """);

        for (MiembroPerfilDTO miembro : miembros) {
            tabla.append(String.format("""
                        <tr>
                            <td>%s</td>
                            <td>%s</td>
                            <td>%s</td>
                        </tr>
                """,
                    miembro.getNombreCompleto(),
                    miembro.getDocumento() != null ? miembro.getDocumento() : "N/A",
                    miembro.getTitulos() != null ? String.join(", ", miembro.getTitulos()) : "Sin perfil"
            ));
        }

        tabla.append("""
                    </tbody>
                </table>
            </div>
            """);

        return tabla.toString();
    }

    private String generarSeccionTurnos(List<TurnoDTO> turnos) {
        StringBuilder tabla = new StringBuilder("""
            <div class="section">
                <div class="section-title">⏰ Turnos Actuales (Últimos 10)</div>
                <table>
                    <thead>
                        <tr>
                            <th>Fecha Inicio</th>
                            <th>Fecha Fin</th>
                            <th>Tipo</th>
                            <th>Jornada</th>
                            <th>Horas</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
            """);

        for (TurnoDTO turno : turnos) {
            tabla.append(String.format("""
                        <tr>
                            <td>%s</td>
                            <td>%s</td>
                            <td>%s</td>
                            <td>%s</td>
                            <td>%.1f</td>
                            <td><span class="%s">%s</span></td>
                        </tr>
                """,
                    turno.getFechaInicio() != null ? turno.getFechaInicio().toString() : "N/A",
                    turno.getFechaFin() != null ? turno.getFechaFin().toString() : "N/A",
                    turno.getTipoTurno(),
                    turno.getJornada(),
                    turno.getTotalHoras() != null ? turno.getTotalHoras() : 0.0,
                    turno.getEstadoTurno() != null && turno.getEstadoTurno().equals("abierto") ? "status-active" : "status-inactive",
                    turno.getEstadoTurno()
            ));
        }

        tabla.append("""
                    </tbody>
                </table>
            </div>
            """);

        return tabla.toString();
    }

    private String generarSeccionHistorialCuadro(List<CambioCuadroDTO> historial) {
        StringBuilder tabla = new StringBuilder("""
            <div class="section">
                <div class="section-title">📊 Historial de Cambios del Cuadro (Últimos 10)</div>
                <table>
                    <thead>
                        <tr>
                            <th>Fecha</th>
                            <th>Versión</th>
                            <th>Estado</th>
                            <th>Turno Excepción</th>
                            <th>Categoría</th>
                        </tr>
                    </thead>
                    <tbody>
            """);

        for (CambioCuadroDTO cambio : historial) {
            tabla.append(String.format("""
                        <tr>
                            <td>%s</td>
                            <td>%s</td>
                            <td><span class="%s">%s</span></td>
                            <td>%s</td>
                            <td>%s</td>
                        </tr>
                """,
                    cambio.getFechaCambio() != null ? cambio.getFechaCambio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A",
                    cambio.getVersion(),
                    cambio.getEstadoCuadro() != null && cambio.getEstadoCuadro().equals("abierto") ? "status-active" : "status-inactive",
                    cambio.getEstadoCuadro(),
                    cambio.getTurnoExcepcion() != null && cambio.getTurnoExcepcion() ? "Sí" : "No",
                    cambio.getCategoria()
            ));
        }

        tabla.append("""
                    </tbody>
                </table>
            </div>
            """);

        return tabla.toString();
    }

    private String generarSeccionHistorialTurnos(List<CambioTurnoDTO> historialTurnos) {
        StringBuilder tabla = new StringBuilder("""
            <div class="section">
                <div class="section-title">🔄 Historial de Cambios de Turnos (Últimos 20)</div>
                <table>
                    <thead>
                        <tr>
                            <th>Fecha Cambio</th>
                            <th>Tipo Turno</th>
                            <th>Jornada</th>
                            <th>Horas</th>
                            <th>Estado</th>
                            <th>Comentarios</th>
                        </tr>
                    </thead>
                    <tbody>
            """);

        for (CambioTurnoDTO cambio : historialTurnos) {
            tabla.append(String.format("""
                        <tr>
                            <td>%s</td>
                            <td>%s</td>
                            <td>%s</td>
                            <td>%.1f</td>
                            <td><span class="%s">%s</span></td>
                            <td>%s</td>
                        </tr>
                """,
                    cambio.getFechaCambio() != null ? cambio.getFechaCambio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A",
                    cambio.getTipoTurno(),
                    cambio.getJornada(),
                    cambio.getTotalHoras() != null ? cambio.getTotalHoras() : 0.0,
                    cambio.getEstadoTurno() != null && cambio.getEstadoTurno().equals("abierto") ? "status-active" : "status-inactive",
                    cambio.getEstadoTurno(),
                    cambio.getComentarios() != null ? cambio.getComentarios() : "Sin comentarios"
            ));
        }

        tabla.append("""
                    </tbody>
                </table>
            </div>
            """);

        return tabla.toString();
    }

    private String generarSeccionHistorialEquipos(List<CambiosEquipoDTO> historialEquipos) {
        StringBuilder tabla = new StringBuilder("""
        <div class="section">
            <div class="section-title">🔧 Historial de Cambios de Equipos (Últimos %d cambios)</div>
            <table>
                <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Equipo</th>
                        <th>Tipo Cambio</th>
                        <th>Cambios</th>
                        <th>Usuario</th>
                        <th>Observaciones</th>
                    </tr>
                </thead>
                <tbody>
        """.formatted(historialEquipos.size()));

        for (CambiosEquipoDTO cambio : historialEquipos) {
            tabla.append(String.format("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td><span class="status-active">%s</span></td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                    </tr>
            """,
                    cambio.getFechaCambio() != null ? cambio.getFechaCambio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A",
                    cambio.getNombreEquipo() != null ? cambio.getNombreEquipo() : "Equipo ID: " + cambio.getIdEquipo(),
                    cambio.getTipoCambio(),
                    cambio.getResumenCambio(),
                    cambio.getUsuarioCambio() != null ? cambio.getUsuarioCambio() : "Sistema",
                    cambio.getObservaciones() != null ? cambio.getObservaciones() : "Sin observaciones"
            ));
        }

        tabla.append("""
                </tbody>
            </table>
        </div>
        """);

        return tabla.toString();
    }

    private String generarSeccionHistorialPersonasEquipo(List<CambiosPersonaEquipoDTO> historialPersonas) {
        StringBuilder tabla = new StringBuilder("""
        <div class="section">
            <div class="section-title">👥 Historial de Cambios Persona-Equipo (Últimos %d cambios)</div>
            <table>
                <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Persona</th>
                        <th>Tipo Cambio</th>
                        <th>Detalles</th>
                        <th>Usuario</th>
                        <th>Observaciones</th>
                    </tr>
                </thead>
                <tbody>
        """.formatted(historialPersonas.size()));

        for (CambiosPersonaEquipoDTO cambio : historialPersonas) {
            tabla.append(String.format("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td><span class="%s">%s</span></td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                    </tr>
            """,
                    cambio.getFechaCambio() != null ? cambio.getFechaCambio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A",
                    cambio.getNombrePersona() != null ? cambio.getNombrePersona() + " (" + cambio.getDocumentoPersona() + ")" : "Persona ID: " + cambio.getIdPersona(),
                    cambio.getTipoCambio().equals("ASIGNACION") ? "status-active" :
                            cambio.getTipoCambio().equals("DESVINCULACION") ? "status-inactive" : "status-active",
                    cambio.getTipoCambio(),
                    cambio.getResumenCambio(),
                    cambio.getUsuarioCambio() != null ? cambio.getUsuarioCambio() : "Sistema",
                    cambio.getObservaciones() != null ? cambio.getObservaciones() : "Sin observaciones"
            ));
        }

        tabla.append("""
                </tbody>
            </table>
        </div>
        """);

        return tabla.toString();
    }

}
