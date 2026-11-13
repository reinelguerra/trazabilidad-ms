package com.plazoleta.trazabilidad.infrastructure.output.jpa.mapper;

import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.entity.LogPedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LogPedidoEntityMapper {

    @Mapping(target = "id", ignore = true)
    LogPedidoEntity toEntity(LogPedido log);

    LogPedido toModel(LogPedidoEntity entity);
}