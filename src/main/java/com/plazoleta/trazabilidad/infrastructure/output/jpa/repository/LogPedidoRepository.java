package com.plazoleta.trazabilidad.infrastructure.output.jpa.repository;

import com.plazoleta.trazabilidad.infrastructure.output.jpa.entity.LogPedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogPedidoRepository extends JpaRepository<LogPedidoEntity, Long> {

    List<LogPedidoEntity> findByIdPedidoOrderByFechaAscIdAsc(Long idPedido);
        List<LogPedidoEntity> findByIdRestauranteOrderByFechaAscIdAsc(Long idRestaurante);
}