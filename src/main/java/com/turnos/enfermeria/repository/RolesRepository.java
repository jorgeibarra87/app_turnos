package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long>, JpaSpecificationExecutor<Roles> {

}