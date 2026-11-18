package com.plazoleta.trazabilidad.infrastructure.input.rest.mapper;

import com.plazoleta.trazabilidad.domain.model.EficienciaEmpleado;
import com.plazoleta.trazabilidad.domain.model.EficienciaPedido;
import com.plazoleta.trazabilidad.infrastructure.input.rest.dto.EficienciaEmpleadoResponseDto;
import com.plazoleta.trazabilidad.infrastructure.input.rest.dto.EficienciaPedidoResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EficienciaRestMapper {

    EficienciaPedidoResponseDto toPedidoResponse(EficienciaPedido pedido);

    EficienciaEmpleadoResponseDto toEmpleadoResponse(EficienciaEmpleado empleado);
}