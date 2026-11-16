package com.plazoleta.trazabilidad.application.usecase;

import com.plazoleta.trazabilidad.domain.api.EficienciaServicePort;
import com.plazoleta.trazabilidad.domain.exception.AccesoDenegadoException;
import com.plazoleta.trazabilidad.domain.exception.ReglaDeNegocioException;
import com.plazoleta.trazabilidad.domain.model.EficienciaEmpleado;
import com.plazoleta.trazabilidad.domain.model.EficienciaPedido;
import com.plazoleta.trazabilidad.domain.model.EstadoPedido;
import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.domain.spi.LogPedidoPersistencePort;
import com.plazoleta.trazabilidad.domain.spi.RestauranteClientPort;
import com.plazoleta.trazabilidad.domain.util.MensajeError;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class EficienciaUseCase implements EficienciaServicePort {

    private final LogPedidoPersistencePort logPedidoPersistencePort;
    private final RestauranteClientPort restauranteClientPort;

    public EficienciaUseCase(LogPedidoPersistencePort logPedidoPersistencePort,
                             RestauranteClientPort restauranteClientPort) {
        this.logPedidoPersistencePort = logPedidoPersistencePort;
        this.restauranteClientPort = restauranteClientPort;
    }

    @Override
    public List<EficienciaPedido> consultarTiempoPorPedido(Long idRestaurante, Long idPropietario) {
        validarAcceso(idRestaurante, idPropietario);
        Map<Long, List<LogPedido>> porPedido = logPedidoPersistencePort.buscarPorIdRestaurante(idRestaurante)
                .stream()
                .collect(Collectors.groupingBy(LogPedido::idPedido, TreeMap::new, Collectors.toList()));
        List<EficienciaPedido> resultado = new ArrayList<>();
        for (List<LogPedido> historial : porPedido.values()) {
            calcular(historial).ifPresent(resultado::add);
        }
        return resultado;
    }

    @Override
    public List<EficienciaEmpleado> consultarRankingEmpleados(Long idRestaurante, Long idPropietario) {
        Map<Long, List<EficienciaPedido>> porEmpleado = consultarTiempoPorPedido(idRestaurante, idPropietario)
                .stream()
                .filter(pedido -> pedido.idEmpleado() != null)
                .collect(Collectors.groupingBy(EficienciaPedido::idEmpleado));
        List<EficienciaEmpleado> ordenados = porEmpleado.entrySet().stream()
                .map(entrada -> new EficienciaEmpleado(
                        0, entrada.getKey(), entrada.getValue().size(), tiempoMedio(entrada.getValue())))
                .sorted(Comparator.comparingLong(EficienciaEmpleado::tiempoMedioSegundos)
                        .thenComparing(EficienciaEmpleado::idEmpleado))
                .toList();
        List<EficienciaEmpleado> ranking = new ArrayList<>();
        for (int i = 0; i < ordenados.size(); i++) {
            EficienciaEmpleado fila = ordenados.get(i);
            ranking.add(new EficienciaEmpleado(
                    i + 1, fila.idEmpleado(), fila.pedidosEntregados(), fila.tiempoMedioSegundos()));
        }
        return ranking;
    }

    private void validarAcceso(Long idRestaurante, Long idPropietario) {
        if (idRestaurante == null || idRestaurante <= 0) {
            throw new ReglaDeNegocioException(MensajeError.ID_RESTAURANTE_INVALIDO);
        }
        Long propietarioDelRestaurante = restauranteClientPort.obtenerIdPropietario(idRestaurante);
        if (propietarioDelRestaurante == null || !propietarioDelRestaurante.equals(idPropietario)) {
            throw new AccesoDenegadoException(MensajeError.RESTAURANTE_AJENO);
        }
    }

    private Optional<EficienciaPedido> calcular(List<LogPedido> historial) {
        Optional<LogPedido> inicio = historial.stream()
                .filter(log -> log.estadoNuevo() == EstadoPedido.PENDIENTE)
                .findFirst();
        Optional<LogPedido> fin = historial.stream()
                .filter(log -> log.estadoNuevo() == EstadoPedido.ENTREGADO)
                .findFirst();
        if (inicio.isEmpty() || fin.isEmpty()) {
            return Optional.empty();
        }
        long segundos = Duration.between(inicio.get().fecha(), fin.get().fecha()).getSeconds();
        return Optional.of(new EficienciaPedido(
                inicio.get().idPedido(), fin.get().idEmpleado(),
                inicio.get().fecha(), fin.get().fecha(), segundos));
    }

    private long tiempoMedio(List<EficienciaPedido> pedidos) {
        double promedio = pedidos.stream().mapToLong(EficienciaPedido::duracionSegundos).average().orElse(0);
        return Math.round(promedio);
    }
}