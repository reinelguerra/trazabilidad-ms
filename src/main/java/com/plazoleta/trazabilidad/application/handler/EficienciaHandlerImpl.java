package com.plazoleta.trazabilidad.application.handler;

import com.plazoleta.trazabilidad.domain.api.EficienciaServicePort;
import com.plazoleta.trazabilidad.domain.model.EficienciaEmpleado;
import com.plazoleta.trazabilidad.domain.model.EficienciaPedido;

import java.util.List;

public class EficienciaHandlerImpl implements EficienciaHandler {

    private final EficienciaServicePort eficienciaServicePort;

    public EficienciaHandlerImpl(EficienciaServicePort eficienciaServicePort) {
        this.eficienciaServicePort = eficienciaServicePort;
    }

    @Override
    public List<EficienciaPedido> consultarTiempoPorPedido(Long idRestaurante, Long idPropietario) {
        return eficienciaServicePort.consultarTiempoPorPedido(idRestaurante, idPropietario);
    }

    @Override
    public List<EficienciaEmpleado> consultarRankingEmpleados(Long idRestaurante, Long idPropietario) {
        return eficienciaServicePort.consultarRankingEmpleados(idRestaurante, idPropietario);
    }
}