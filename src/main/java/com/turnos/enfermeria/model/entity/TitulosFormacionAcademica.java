package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Data;
import jakarta.persistence.*;

import java.util.List;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "titulos_formacion_academica", schema = "public")
public class TitulosFormacionAcademica {

    @Id
    @Column(name = "id_titulo", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTitulo;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_tipo_formacion_academica", referencedColumnName = "id_tipo_formacion_academica")
    @JsonIncludeProperties("tipo")
    private TipoFormacionAcademica tipoFormacionAcademica;

    public Long getIdTipoFormacionAcademica() {
        return tipoFormacionAcademica != null ? tipoFormacionAcademica.getIdTipoFormacionAcademica() : null;
    }

    public String getNombreTipo() {
        return tipoFormacionAcademica != null ? tipoFormacionAcademica.getTipo() : null;
    }
}
