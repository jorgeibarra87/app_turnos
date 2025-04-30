package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * $table.getTableComment()
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "gestor_contrato", schema = "public")
public class GestorContrato {

    @Id
    @Column(name = "id_gestor_contrato", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGestorContrato;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_contrato", referencedColumnName = "id_contrato")
    private Contrato contrato;

    public Long getIdContrato() {
        return contrato != null ? contrato.getIdContrato() : null;
    }
    public Long getIdPersona() { return usuario != null ? usuario.getIdPersona() : null;}
}
