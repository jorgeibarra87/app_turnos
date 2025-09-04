package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "configuracion_correos")
public class ConfiguracionCorreos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Long idConfiguracion;

    @Column(name = "correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "tipo_correo", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoCorreo tipoCorreo; // ✅ Ahora usa el enum externo

    @Column(name = "activo")
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
