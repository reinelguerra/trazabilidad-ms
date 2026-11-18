package com.plazoleta.trazabilidad.infrastructure.output.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

/** Reenvía el Authorization de la petición que se está atendiendo a la llamada saliente. */
public class ReenviarAutorizacionInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        RequestAttributes atributos = RequestContextHolder.getRequestAttributes();
        if (atributos instanceof ServletRequestAttributes servlet) {
            String autorizacion = servlet.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (autorizacion != null) {
                request.getHeaders().set(HttpHeaders.AUTHORIZATION, autorizacion);
            }
        }
        return execution.execute(request, body);
    }
}