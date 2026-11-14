package com.plazoleta.trazabilidad.domain.spi;

import com.plazoleta.trazabilidad.domain.model.LogPedido;

public interface LogPedidoPersistencePort {

    LogPedido guardarLog(LogPedido log);
}