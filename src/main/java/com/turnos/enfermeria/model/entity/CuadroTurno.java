package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * $table.getTableComment()
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "cuadro_turno", schema = "public")
public class CuadroTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuadro_turno", nullable = false)
    private Long idCuadroTurno;

    @ManyToOne
    @JoinColumn(name = "id_macroproceso", referencedColumnName = "id_macroproceso")
    @JsonIncludeProperties("nombre")
    private Macroprocesos macroProcesos;

    @ManyToOne
    @JoinColumn(name = "id_proceso", referencedColumnName = "id_proceso")
    @JsonIncludeProperties("nombre")
    private Procesos procesos;

    @ManyToOne
    @JoinColumn(name = "id_servicio", referencedColumnName = "id_servicio")
    @JsonIncludeProperties("nombre")
    private Servicio servicios;

    @ManyToOne
    @JoinColumn(name = "id_seccion_servicio", referencedColumnName = "id_seccion_servicio")
    @JsonIncludeProperties("nombre")
    private SeccionesServicio seccionesServicios;

    @ManyToOne
    @JoinColumn(name = "id_subseccion_servicio", referencedColumnName = "id_subseccion_servicio")
    @JsonIncludeProperties("nombre")
    private SubseccionesServicio subseccionesServicios;

    @ManyToOne(fetch = FetchType.EAGER) // asegúrate de que se cargue
    @JoinColumn(name = "id_equipo", referencedColumnName = "id_equipo")
    @JsonIncludeProperties("nombre")
    private Equipo equipos;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "anio")
    private String anio;

    @Column(name = "mes")
    private String mes;

    //abierto,cerrado
    @Column(name = "estado_cuadro")
    private String estadoCuadro = "abierto";

    //v01_0225
    @Column(name = "version")
    private String version;

    @Column(name = "turno_excepcion")
    private Boolean turnoExcepcion = false;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "estado")
    private Boolean estado;


    // Métodos de conveniencia para obtener IDs
    public Long getIdMacroproceso() {
        return macroProcesos != null ? macroProcesos.getIdMacroproceso() : null;
    }

    public Long getIdProceso() {
        return procesos != null ? procesos.getIdProceso() : null;
    }

    public Long getIdServicio() {
        return servicios != null ? servicios.getIdServicio() : null;
    }

    public Long getIdSeccionServicio() {
        return seccionesServicios != null ? seccionesServicios.getIdSeccionServicio() : null;
    }

    public Long getIdSubeccionServicio() {
        return subseccionesServicios != null ? subseccionesServicios.getIdSubseccionServicio() : null;
    }

    public Long getIdEquipo() {
        return equipos != null ? equipos.getIdEquipo() : null;
    }
    public String getNombreEquipo() {
        return equipos != null ? equipos.getNombre() : null;
    }
    public String getNombreProceso() {
        return procesos != null ? procesos.getNombre() : null;
    }
    public String getNombreServicio() {
        return servicios != null ? servicios.getNombre() : null;
    }
    public String getNombreSeccionServicio() {
        return seccionesServicios != null ? seccionesServicios.getNombre() : null;
    }
    public String getNombreSubseccionServicio() {
        return subseccionesServicios != null ? subseccionesServicios.getNombre() : null;
    }

    public String getNombreMacroproceso() {
        return macroProcesos != null ? macroProcesos.getNombre() : null;
    }

}
