package com.plazoleta.trazabilidad.infrastructure.config;

import com.plazoleta.trazabilidad.application.handler.EficienciaHandler;
import com.plazoleta.trazabilidad.application.handler.EficienciaHandlerImpl;
import com.plazoleta.trazabilidad.application.usecase.EficienciaUseCase;
import com.plazoleta.trazabilidad.domain.api.EficienciaServicePort;
import com.plazoleta.trazabilidad.domain.spi.LogPedidoPersistencePort;
import com.plazoleta.trazabilidad.domain.spi.RestauranteClientPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EficienciaBeanConfiguration {

    @Bean
    public EficienciaServicePort eficienciaServicePort(LogPedidoPersistencePort logPedidoPersistencePort,
                                                       RestauranteClientPort restauranteClientPort) {
        return new EficienciaUseCase(logPedidoPersistencePort, restauranteClientPort);
    }

    @Bean
    public EficienciaHandler eficienciaHandler(EficienciaServicePort eficienciaServicePort) {
        return new EficienciaHandlerImpl(eficienciaServicePort);
    }
}