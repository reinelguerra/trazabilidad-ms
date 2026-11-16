package com.plazoleta.trazabilidad.infrastructure.output.http;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ReenviarAutorizacionInterceptorTest {

    @AfterEach
    void limpiar() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void reenviaElAuthorizationDeLaPeticionEnCurso() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("http://plazoleta.test/api/v1")
                .requestInterceptor(new ReenviarAutorizacionInterceptor());
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();
        MockHttpServletRequest peticion = new MockHttpServletRequest();
        peticion.addHeader("Authorization", "Bearer abc");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(peticion));
        servidor.expect(requestTo("http://plazoleta.test/api/v1/ping"))
                .andExpect(header("Authorization", "Bearer abc"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        builder.build().get().uri("/ping").retrieve().toBodilessEntity();

        servidor.verify();
    }

    @Test
    void sinPeticionEnCurso_noAgregaElHeader() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("http://plazoleta.test/api/v1")
                .requestInterceptor(new ReenviarAutorizacionInterceptor());
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();
        servidor.expect(requestTo("http://plazoleta.test/api/v1/ping"))
                .andExpect(headerDoesNotExist("Authorization"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        builder.build().get().uri("/ping").retrieve().toBodilessEntity();

        servidor.verify();
    }
}