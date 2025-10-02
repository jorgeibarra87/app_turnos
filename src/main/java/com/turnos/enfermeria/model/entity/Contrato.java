package com.turnos.enfermeria.model.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contrato", schema = "public")
public class Contrato {
    @Id
    @Column(name = "id_contrato", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrato;

    @Column(name = "num_contrato", nullable = false, unique = true)
    private String numContrato;

    @Column(name = "supervisor", nullable = false)
    private String supervisor;

    @Column(name = "Apoyo_supervision", nullable = false)
    private String apoyoSupervision;

    @Column(name = "objeto", nullable = false)
    private String objeto;

    @Column(name = "contratista", nullable = false)
    private String contratista;

    @Column(name = "fecha_inicio")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @Column(name = "fecha_terminacion")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaTerminacion;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "contratos_titulos", joinColumns = @JoinColumn(name = "idContrato", referencedColumnName = "id_contrato"),
            inverseJoinColumns = @JoinColumn(name = "idTitulo", referencedColumnName = "id_titulo")
    )
    private List<TitulosFormacionAcademica> titulosFormacionAcademica;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "procesos_contrato", joinColumns = @JoinColumn(name = "idContrato", referencedColumnName = "id_contrato"),
            inverseJoinColumns = @JoinColumn(name = "idProceso", referencedColumnName = "id_proceso")
    )
    private List<Procesos> procesos;

    @Column(name = "observaciones", length = 3000)
    private String observaciones;
}
