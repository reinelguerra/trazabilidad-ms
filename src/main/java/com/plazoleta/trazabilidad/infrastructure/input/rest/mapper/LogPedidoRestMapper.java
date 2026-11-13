package com.plazoleta.trazabilidad.infrastructure.input.rest.mapper;

import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.infrastructure.input.rest.dto.LogPedidoRequestDto;
import com.plazoleta.trazabilidad.infrastructure.input.rest.dto.LogPedidoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LogPedidoRestMapper {

    @Mapping(target = "fecha", ignore = true)
    LogPedido toModel(LogPedidoRequestDto request);

    LogPedidoResponseDto toResponse(LogPedido log);
}