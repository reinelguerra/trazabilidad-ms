package com.plazoleta.trazabilidad.infrastructure.config;

import com.plazoleta.trazabilidad.application.handler.LogPedidoHandler;
import com.plazoleta.trazabilidad.application.handler.LogPedidoHandlerImpl;
import com.plazoleta.trazabilidad.application.usecase.LogPedidoUseCase;
import com.plazoleta.trazabilidad.domain.api.LogPedidoServicePort;
import com.plazoleta.trazabilidad.domain.spi.LogPedidoPersistencePort;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.adapter.LogPedidoJpaAdapter;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.mapper.LogPedidoEntityMapper;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.repository.LogPedidoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class LogPedidoBeanConfiguration {

    @Bean
    public Clock reloj() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public LogPedidoPersistencePort logPedidoPersistencePort(LogPedidoRepository logPedidoRepository,
                                                             LogPedidoEntityMapper logPedidoEntityMapper) {
        return new LogPedidoJpaAdapter(logPedidoRepository, logPedidoEntityMapper);
    }

    @Bean
    public LogPedidoServicePort logPedidoServicePort(LogPedidoPersistencePort logPedidoPersistencePort,
                                                     Clock reloj) {
        return new LogPedidoUseCase(logPedidoPersistencePort, reloj);
    }

    @Bean
    public LogPedidoHandler logPedidoHandler(LogPedidoServicePort logPedidoServicePort) {
        return new LogPedidoHandlerImpl(logPedidoServicePort);
    }
}