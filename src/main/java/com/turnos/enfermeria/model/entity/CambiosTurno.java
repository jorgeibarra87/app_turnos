package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "cambios_turno", schema = "public")
public class CambiosTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cambio", nullable = false)
    private Long idCambio;

    @ManyToOne
    @JoinColumn(name = "id_turno", referencedColumnName = "id_turno")
    private Turnos turno;

    @ManyToOne
    @JoinColumn(name = "id_cuadro_turno", referencedColumnName = "id_cuadro_turno")
    private CuadroTurno cuadroTurno;

    @ManyToOne
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona")
    private Usuario usuario;

    @Column(name = "fecha_cambio")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCambio;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "totalHoras")
    private Long totalHoras;

    @Column(name = "tipo_turno")
    private String tipoTurno;

    @Column(name = "estado_turno")
    private String estadoTurno;

    @Column(name = "jornada")
    private String jornada;

    @Column(name = "version")
    private String version;

    @Column(name = "comentarios")
    private String comentarios;

    @Column(name = "estado")
    private Boolean estado;

    public Long getIdTurno() {
        return turno != null ? turno.getIdTurno() : null;
    }

    public Long getIdCuadroTurno() {
        return cuadroTurno != null ? cuadroTurno.getIdCuadroTurno() : null;
    }

    public Long getIdPersona() {
        return usuario != null ? usuario.getIdPersona() : null;
    }
}