package com.plazoleta.trazabilidad.infrastructure.output.http.adapter;

import com.plazoleta.trazabilidad.domain.exception.RestauranteNoEncontradoException;
import com.plazoleta.trazabilidad.infrastructure.exception.ServicioPlazoletaNoDisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpMethod;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


class RestauranteClientAdapterTest {

    private static final String URL = "http://plazoleta.test/api/v1/restaurantes/3";

    private MockRestServiceServer servidor;
    private RestauranteClientAdapter adaptador;

    @BeforeEach
    void preparar() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://plazoleta.test/api/v1");
        servidor = MockRestServiceServer.bindTo(builder).build();
        adaptador = new RestauranteClientAdapter(builder.build());
    }

    @Test
    void devuelveElIdDelPropietario_ignorandoLosDemasCampos() {
        servidor.expect(requestTo(URL)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":3,\"nombre\":\"Sabor 24\",\"nit\":\"900123456\","
                        + "\"direccion\":\"Calle 10\",\"telefono\":\"+573001234567\","
                        + "\"urlLogo\":\"https://logo.com/s.png\",\"idPropietario\":20}",
                        MediaType.APPLICATION_JSON));

        assertEquals(20L, adaptador.obtenerIdPropietario(3L));
        servidor.verify();
    }

    @Test
    void restauranteInexistente_lanzaRestauranteNoEncontrado() {
        servidor.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(RestauranteNoEncontradoException.class, () -> adaptador.obtenerIdPropietario(3L));
    }

    @Test
    void plazoletaConError500_lanzaServicioNoDisponible() {
        servidor.expect(requestTo(URL)).andRespond(withServerError());

        assertThrows(ServicioPlazoletaNoDisponibleException.class, () -> adaptador.obtenerIdPropietario(3L));
    }

    @Test
    void plazoletaCaida_lanzaServicioNoDisponible() {
        servidor.expect(requestTo(URL)).andRespond(request -> {
            throw new IOException("sin conexion");
        });

        assertThrows(ServicioPlazoletaNoDisponibleException.class, () -> adaptador.obtenerIdPropietario(3L));
    }
}