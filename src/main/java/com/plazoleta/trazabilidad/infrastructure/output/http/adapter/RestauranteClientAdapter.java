package com.plazoleta.trazabilidad.infrastructure.output.http.adapter;

import com.plazoleta.trazabilidad.domain.exception.RestauranteNoEncontradoException;
import com.plazoleta.trazabilidad.domain.spi.RestauranteClientPort;
import com.plazoleta.trazabilidad.domain.util.MensajeError;
import com.plazoleta.trazabilidad.infrastructure.exception.ServicioPlazoletaNoDisponibleException;
import com.plazoleta.trazabilidad.infrastructure.output.http.ReenviarAutorizacionInterceptor;
import com.plazoleta.trazabilidad.infrastructure.output.http.dto.RestauranteRespuesta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Llama a plazoleta-ms reenviando el token del propietario que hizo la consulta. */
@Component
public class RestauranteClientAdapter implements RestauranteClientPort {

    private final RestClient restClient;

    @Autowired
    public RestauranteClientAdapter(
            @Value("${plazoleta.service.url:http://localhost:8082/api/v1}") String baseUrl) {
        this(RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(new ReenviarAutorizacionInterceptor())
                .build());
    }

    RestauranteClientAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Long obtenerIdPropietario(Long idRestaurante) {
        try {
            RestauranteRespuesta restaurante = restClient.get()
                    .uri("/restaurantes/{id}", idRestaurante)
                    .retrieve()
                    .body(RestauranteRespuesta.class);
            return restaurante == null ? null : restaurante.idPropietario();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RestauranteNoEncontradoException(MensajeError.RESTAURANTE_NO_ENCONTRADO);
        } catch (RestClientException e) {
            throw new ServicioPlazoletaNoDisponibleException(MensajeError.PLAZOLETA_NO_DISPONIBLE, e);
        }
    }
}