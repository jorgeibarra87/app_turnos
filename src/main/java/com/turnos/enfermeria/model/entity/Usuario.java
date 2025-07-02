package com.turnos.enfermeria.model.entity;

import com.turnos.enfermeria.model.entity.Roles;
import com.turnos.enfermeria.model.entity.Servicio;
import lombok.Data;
import jakarta.persistence.*;

import java.util.List;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "usuario", schema = "public")
public class Usuario {

    @Id
    @Column(name = "id_persona", nullable = false)
    private Long idPersona;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "password")
    private String password;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_persona")
    private Persona persona;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles", joinColumns = @JoinColumn(name = "idPersona", referencedColumnName = "id_persona"),
            inverseJoinColumns = @JoinColumn(name = "rolId", referencedColumnName = "id_rol")
    )
    private List<Roles> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_titulos", joinColumns = @JoinColumn(name = "idPersona", referencedColumnName = "id_persona"),
            inverseJoinColumns = @JoinColumn(name = "idTitulo", referencedColumnName = "id_titulo")
    )
    private List<TitulosFormacionAcademica> titulosFormacionAcademica;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_equipo", joinColumns = @JoinColumn(name = "idPersona", referencedColumnName = "id_persona"),
            inverseJoinColumns = @JoinColumn(name = "idEquipo", referencedColumnName = "id_equipo")
    )
    private List<Equipo> equipos;

}
