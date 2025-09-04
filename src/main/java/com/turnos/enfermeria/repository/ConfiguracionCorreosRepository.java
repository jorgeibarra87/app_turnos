package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.ConfiguracionCorreos;
import com.turnos.enfermeria.model.entity.TipoCorreo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionCorreosRepository extends JpaRepository<ConfiguracionCorreos, Long> {

    List<ConfiguracionCorreos> findByActivoTrue();

    List<ConfiguracionCorreos> findByTipoCorreoAndActivoTrue(TipoCorreo tipoCorreo);

    boolean existsByCorreo(String correo);

    Optional<ConfiguracionCorreos> findByCorreo(String correo);
}

