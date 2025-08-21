package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * $table.getTableComment()
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tipo_atencion", schema = "public")
public class TipoAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_atencion", nullable = false)
    private Long idTipoAtencion;

    @Column(name = "nombre_tipo_atencion")
    private String nombreTipoAtencion;

    @Column(name = "descripcion_tipo_atencion" )
    private String descripcionTipoAtencion;

    @Column(name = "estado_tipo_atencion")
    private String estadoTipoAtencion;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_contrato", referencedColumnName = "id_contrato")
    private Contrato contratos;

    public Long getIdContrato() {
        return contratos != null ? contratos.getIdContrato() : null;
    }
}
