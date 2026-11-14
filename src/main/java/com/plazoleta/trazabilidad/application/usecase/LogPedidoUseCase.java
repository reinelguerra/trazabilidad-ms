package com.plazoleta.trazabilidad.application.usecase;

import com.plazoleta.trazabilidad.domain.api.LogPedidoServicePort;
import com.plazoleta.trazabilidad.domain.exception.ReglaDeNegocioException;
import com.plazoleta.trazabilidad.domain.model.EstadoPedido;
import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.domain.spi.LogPedidoPersistencePort;
import com.plazoleta.trazabilidad.domain.util.MensajeError;

import java.time.Clock;
import java.time.LocalDateTime;

public class LogPedidoUseCase implements LogPedidoServicePort {

    private final LogPedidoPersistencePort logPedidoPersistencePort;
    private final Clock reloj;

    public LogPedidoUseCase(LogPedidoPersistencePort logPedidoPersistencePort, Clock reloj) {
        this.logPedidoPersistencePort = logPedidoPersistencePort;
        this.reloj = reloj;
    }

    @Override
    public LogPedido registrarLog(LogPedido log) {
        validarCambioDeEstado(log);
        LogPedido conFecha = new LogPedido(
                log.idPedido(),
                log.idCliente(),
                log.idRestaurante(),
                log.idEmpleado(),
                log.estadoAnterior(),
                log.estadoNuevo(),
                LocalDateTime.now(reloj));
        return logPedidoPersistencePort.guardarLog(conFecha);
    }

    private void validarCambioDeEstado(LogPedido log) {
        if (log.estadoAnterior() == null && log.estadoNuevo() != EstadoPedido.PENDIENTE) {
            throw new ReglaDeNegocioException(MensajeError.PRIMER_LOG_DEBE_SER_PENDIENTE);
        }
        if (log.estadoAnterior() == log.estadoNuevo()) {
            throw new ReglaDeNegocioException(MensajeError.ESTADO_REPETIDO);
        }
    }
}