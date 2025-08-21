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
@Table(name = "tipo_turno", schema = "public")
public class TipoTurno {

//    public TipoTurno(TipoTurno tipoTurno) {
//        this.especialidad = tipoTurno.getEspecialidad();
//        this.presencial = tipoTurno.isPresencial();
//        this.disponibilidad = tipoTurno.isDisponibilidad();
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_turno", nullable = false)
    private Long idTipoTurno;

    @Column(name = "especialidad")
    private String especialidad;

    @Column(name = "presencial" )
    private boolean presencial;

    @Column(name = "disponibilidad")
    private boolean disponibilidad;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_contrato", referencedColumnName = "id_contrato")
    private Contrato contratos;

    public Long getIdContrato() {
        return contratos != null ? contratos.getIdContrato() : null;
    }

}
