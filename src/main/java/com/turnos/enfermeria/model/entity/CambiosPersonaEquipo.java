package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cambios_persona_equipo", schema = "public")
public class CambiosPersonaEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cambio_persona_equipo")
    private Long idCambioPersonaEquipo;

    @ManyToOne
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona")
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "id_equipo", referencedColumnName = "id_equipo")
    private Equipo equipo;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;

    @Column(name = "tipo_cambio", nullable = false, length = 50)
    private String tipoCambio; // 'ASIGNACION', 'REASIGNACION', 'DESVINCULACION'

    @ManyToOne
    @JoinColumn(name = "equipo_anterior_id", referencedColumnName = "id_equipo")
    private Equipo equipoAnterior;

    @ManyToOne
    @JoinColumn(name = "equipo_nuevo_id", referencedColumnName = "id_equipo")
    private Equipo equipoNuevo;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "usuario_cambio", length = 100)
    private String usuarioCambio;

    @PrePersist
    private void onCreate() {
        if (fechaCambio == null) {
            fechaCambio = LocalDateTime.now();
        }
    }
}
