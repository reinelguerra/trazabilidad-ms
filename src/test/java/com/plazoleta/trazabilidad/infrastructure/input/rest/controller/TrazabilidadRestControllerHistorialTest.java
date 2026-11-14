package com.plazoleta.trazabilidad.infrastructure.input.rest.controller;

import com.plazoleta.trazabilidad.application.handler.LogPedidoHandler;
import com.plazoleta.trazabilidad.domain.exception.AccesoDenegadoException;
import com.plazoleta.trazabilidad.domain.exception.PedidoSinRegistrosException;
import com.plazoleta.trazabilidad.domain.exception.ReglaDeNegocioException;
import com.plazoleta.trazabilidad.domain.model.EstadoPedido;
import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.domain.model.RolUsuario;
import com.plazoleta.trazabilidad.domain.util.MensajeError;
import com.plazoleta.trazabilidad.infrastructure.exception.GlobalExceptionHandler;
import com.plazoleta.trazabilidad.infrastructure.input.rest.mapper.LogPedidoRestMapper;
import com.plazoleta.trazabilidad.infrastructure.security.UsuarioAutenticado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrazabilidadRestControllerHistorialTest {

    private static final String RUTA = "/api/v1/trazabilidad/";
    private static final Long ID_CLIENTE = 4L;

    @Mock
    private LogPedidoHandler logPedidoHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LogPedidoRestMapper mapper = Mappers.getMapper(LogPedidoRestMapper.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrazabilidadRestController(logPedidoHandler, mapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        UsuarioAutenticado cliente = new UsuarioAutenticado(ID_CLIENTE, "cliente@correo.com", RolUsuario.CLIENTE);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(cliente, null, List.of()));
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void consultarHistorial_devuelve200ConLosLogsEnOrdenYUsaElIdDelToken() throws Exception {
        when(logPedidoHandler.consultarHistorial(15L, ID_CLIENTE)).thenReturn(List.of(
                new LogPedido(15L, ID_CLIENTE, 1L, null, null, EstadoPedido.PENDIENTE,
                        LocalDateTime.of(2026, 11, 13, 10, 0)),
                new LogPedido(15L, ID_CLIENTE, 1L, 7L, EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION,
                        LocalDateTime.of(2026, 11, 13, 10, 5))));

        mockMvc.perform(get(RUTA + "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].estadoNuevo").value("PENDIENTE"))
                .andExpect(jsonPath("$[1].estadoAnterior").value("PENDIENTE"))
                .andExpect(jsonPath("$[1].estadoNuevo").value("EN_PREPARACION"))
                .andExpect(jsonPath("$[1].idEmpleado").value(7));

        verify(logPedidoHandler).consultarHistorial(15L, ID_CLIENTE);
    }

    @Test
    void consultarHistorial_sinRegistros_devuelve404() throws Exception {
        when(logPedidoHandler.consultarHistorial(15L, ID_CLIENTE))
                .thenThrow(new PedidoSinRegistrosException(MensajeError.PEDIDO_SIN_REGISTROS));

        mockMvc.perform(get(RUTA + "15"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.PEDIDO_SIN_REGISTROS))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void consultarHistorial_deOtroCliente_devuelve403() throws Exception {
        when(logPedidoHandler.consultarHistorial(15L, ID_CLIENTE))
                .thenThrow(new AccesoDenegadoException(MensajeError.PEDIDO_AJENO));

        mockMvc.perform(get(RUTA + "15"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.PEDIDO_AJENO))
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void consultarHistorial_conIdNoNumerico_devuelve400YNoLlamaAlHandler() throws Exception {
        mockMvc.perform(get(RUTA + "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.ID_PEDIDO_INVALIDO))
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(logPedidoHandler);
    }

    @Test
    void consultarHistorial_cuandoElDominioRechazaElId_devuelve400() throws Exception {
        when(logPedidoHandler.consultarHistorial(0L, ID_CLIENTE))
                .thenThrow(new ReglaDeNegocioException(MensajeError.ID_PEDIDO_INVALIDO));

        mockMvc.perform(get(RUTA + "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.ID_PEDIDO_INVALIDO));
    }
}