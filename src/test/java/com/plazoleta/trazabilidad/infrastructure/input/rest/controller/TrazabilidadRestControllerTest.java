package com.plazoleta.trazabilidad.infrastructure.input.rest.controller;

import com.plazoleta.trazabilidad.application.handler.LogPedidoHandler;
import com.plazoleta.trazabilidad.domain.exception.ReglaDeNegocioException;
import com.plazoleta.trazabilidad.domain.model.EstadoPedido;
import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.domain.util.MensajeError;
import com.plazoleta.trazabilidad.infrastructure.exception.GlobalExceptionHandler;
import com.plazoleta.trazabilidad.infrastructure.input.rest.mapper.LogPedidoRestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrazabilidadRestControllerTest {

    private static final String RUTA = "/api/v1/trazabilidad";

    @Mock
    private LogPedidoHandler logPedidoHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LogPedidoRestMapper mapper = Mappers.getMapper(LogPedidoRestMapper.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrazabilidadRestController(logPedidoHandler, mapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private org.springframework.test.web.servlet.ResultActions enviar(String cuerpo) throws Exception {
        return mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(cuerpo));
    }

    @Test
    void registrarLog_conDatosValidos_devuelve201ConElLogGuardado() throws Exception {
        when(logPedidoHandler.registrarLog(any(LogPedido.class))).thenReturn(new LogPedido(
                15L, 4L, 1L, null, null, EstadoPedido.PENDIENTE, LocalDateTime.of(2026, 11, 13, 16, 0)));

        enviar("{\"idPedido\":15,\"idCliente\":4,\"idRestaurante\":1,\"estadoNuevo\":\"PENDIENTE\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPedido").value(15))
                .andExpect(jsonPath("$.idCliente").value(4))
                .andExpect(jsonPath("$.estadoNuevo").value("PENDIENTE"))
                .andExpect(jsonPath("$.fecha").exists());
    }

    @Test
    void registrarLog_sinIdPedido_devuelve400YNoLlamaAlHandler() throws Exception {
        enviar("{\"idCliente\":4,\"idRestaurante\":1,\"estadoNuevo\":\"PENDIENTE\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.ID_PEDIDO_INVALIDO))
                .andExpect(jsonPath("$.status").value(400));

        verify(logPedidoHandler, never()).registrarLog(any(LogPedido.class));
    }

    @Test
    void registrarLog_conIdPedidoNegativo_devuelve400() throws Exception {
        enviar("{\"idPedido\":-3,\"idCliente\":4,\"idRestaurante\":1,\"estadoNuevo\":\"PENDIENTE\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.ID_PEDIDO_INVALIDO));

        verify(logPedidoHandler, never()).registrarLog(any(LogPedido.class));
    }

    @Test
    void registrarLog_sinEstadoNuevo_devuelve400() throws Exception {
        enviar("{\"idPedido\":15,\"idCliente\":4,\"idRestaurante\":1}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.ESTADO_NUEVO_OBLIGATORIO));

        verify(logPedidoHandler, never()).registrarLog(any(LogPedido.class));
    }

    @Test
    void registrarLog_conVariosErrores_losDevuelveJuntosYOrdenados() throws Exception {
        enviar("{\"idRestaurante\":1,\"estadoNuevo\":\"PENDIENTE\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value(MensajeError.ID_CLIENTE_INVALIDO + "; " + MensajeError.ID_PEDIDO_INVALIDO));
    }

    @Test
    void registrarLog_conEstadoDesconocido_devuelve400ConMensajeDeCuerpoInvalido() throws Exception {
        enviar("{\"idPedido\":15,\"idCliente\":4,\"idRestaurante\":1,\"estadoNuevo\":\"VOLANDO\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.CUERPO_INVALIDO));

        verify(logPedidoHandler, never()).registrarLog(any(LogPedido.class));
    }

    @Test
    void registrarLog_cuandoElDominioRechaza_devuelve400ConLaRegla() throws Exception {
        when(logPedidoHandler.registrarLog(any(LogPedido.class)))
                .thenThrow(new ReglaDeNegocioException(MensajeError.ESTADO_REPETIDO));

        enviar("{\"idPedido\":15,\"idCliente\":4,\"idRestaurante\":1,"
                + "\"estadoAnterior\":\"LISTO\",\"estadoNuevo\":\"LISTO\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.ESTADO_REPETIDO))
                .andExpect(jsonPath("$.status").value(400));
    }
}