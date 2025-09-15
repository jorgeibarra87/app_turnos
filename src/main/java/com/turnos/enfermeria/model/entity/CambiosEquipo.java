package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cambios_equipo", schema = "public")
public class CambiosEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cambio_equipo")
    private Long idCambioEquipo;

    @ManyToOne
    @JoinColumn(name = "id_equipo", referencedColumnName = "id_equipo")
    private Equipo equipo;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;

    @Column(name = "tipo_cambio")
    private String tipoCambio;

    @Column(name = "nombre_anterior")
    private String nombreAnterior;

    @Column(name = "nombre_nuevo")
    private String nombreNuevo;

    @Column(name = "estado_anterior")
    private Boolean estadoAnterior;

    @Column(name = "estado_nuevo")
    private Boolean estadoNuevo;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "usuario_cambio")
    private String usuarioCambio;
}

