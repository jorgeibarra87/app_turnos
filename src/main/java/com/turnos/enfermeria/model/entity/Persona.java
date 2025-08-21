package com.turnos.enfermeria.model.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.sql.Date;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "persona", schema = "public")
public class Persona {

    @Id
    @Column(name = "id_persona", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersona;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "documento")
    private String documento;

    @Column(name = "email")
    private String email;

    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "estado")
    private Boolean estado;
}
