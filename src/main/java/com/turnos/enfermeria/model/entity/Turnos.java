package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * $table.getTableComment()
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "turnos", schema = "public")
public class Turnos {

    @Id
    @Column(name = "id_turno", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTurno;

    @ManyToOne
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona")
    @JsonIncludeProperties("nombre")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_cuadro_turno", referencedColumnName = "id_cuadro_turno")
    private CuadroTurno cuadroTurno;

    @Column(name = "fecha_inicio")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaFin;

    @Column(name = "total_horas")
    private Long totalHoras;

    //presencial,disponibilidad,sabado,domingo,festivo
    @Column(name = "tipo_turno")
    private String tipoTurno;

    //pendiente,cancelado,confirmado
    @Column(name = "estado_turno")
    private String estadoTurno;

    //mañana,tarde,noche
    @Column(name = "jornada")
    private String jornada;

    //v01_0225
    @Column(name = "version")
    private String version;

    @Column(name = "comentarios")
    private String comentarios;

    @Column(name = "estado")
    private Boolean estado;

    public Long getIdPersona() {
        return usuario != null ? usuario.getIdPersona() : null;
    }
    public String getNombrePersona() {
        return usuario != null ? usuario.getPersona().getNombreCompleto() : null;
    }
}

//    @PrePersist
//    @PreUpdate
//    private void calcularTotalHorasYVersionAntesDeGuardar() {
//        if (fechaInicio != null && fechaFin != null) {
//            totalHoras = calcularHorasTrabajadas(fechaInicio, fechaFin);
//        }
//
//        if (version == null || version.isEmpty()) {
//            this.version = generarVersion();
//        }
//    }
//
//    private long calcularHorasTrabajadas(LocalDateTime inicio, LocalDateTime fin) {
//        if (inicio == null || fin == null) {
//            return 0;
//        }
//        return Duration.between(inicio, fin).toHours();
//    }
//
//    private String generarVersion() {
//        String fechaFormato = fechaInicio.format(DateTimeFormatter.ofPattern("MMyy"));
//        return fechaFormato + "_v1";
//    }




