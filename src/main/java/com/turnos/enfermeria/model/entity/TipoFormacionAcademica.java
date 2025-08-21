package com.turnos.enfermeria.model.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.util.List;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "tipo_formacion_academica", schema = "public")
public class TipoFormacionAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_formacion_academica", nullable = false)
    private Long idTipoFormacionAcademica;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "estado")
    private Boolean estado;

}
