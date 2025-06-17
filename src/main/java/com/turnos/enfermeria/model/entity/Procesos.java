package com.turnos.enfermeria.model.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.util.List;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "procesos", schema = "public")
public class Procesos {

    @Id
    @Column(name = "id_proceso", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProceso;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_macroproceso", referencedColumnName = "id_macroproceso")
    private Macroprocesos macroprocesos;

    public Long getIdMacroproceso() {
        return macroprocesos != null ? macroprocesos.getIdMacroproceso() : null;
    }

}
