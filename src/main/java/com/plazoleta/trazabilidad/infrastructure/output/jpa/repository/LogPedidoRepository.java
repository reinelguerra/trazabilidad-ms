package com.plazoleta.trazabilidad.infrastructure.output.jpa.repository;

import com.plazoleta.trazabilidad.infrastructure.output.jpa.entity.LogPedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogPedidoRepository extends JpaRepository<LogPedidoEntity, Long> {
}