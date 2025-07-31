package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.Data;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "procesos_contrato", schema = "public")
public class ProcesosContrato {

    @Id
    @Column(name = "id_proceso_contrato", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProcesoContrato;

    @Column(name = "detalle", nullable = false)
    private String detalle;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_proceso", referencedColumnName = "id_proceso")
    @JsonIncludeProperties("nombre")
    private Procesos procesos;

    @ManyToOne
    @JoinColumn(name = "id_contrato", referencedColumnName = "id_contrato")
    private Contrato contrato;

    public Long getIdProceso() {
        return procesos != null ? procesos.getIdProceso() : null;
    }

    public Long getIdContrato() {
        return contrato != null ? contrato.getIdContrato() : null;
    }
}
