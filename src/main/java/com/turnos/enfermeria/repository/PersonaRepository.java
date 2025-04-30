package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long>, JpaSpecificationExecutor<Persona> {

    Optional<Persona> findByDocumento(String documento);
}
