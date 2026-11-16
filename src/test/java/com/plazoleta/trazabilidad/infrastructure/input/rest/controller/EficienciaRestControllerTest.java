package com.plazoleta.trazabilidad.infrastructure.input.rest.controller;

import com.plazoleta.trazabilidad.application.handler.EficienciaHandler;
import com.plazoleta.trazabilidad.domain.exception.AccesoDenegadoException;
import com.plazoleta.trazabilidad.domain.exception.ReglaDeNegocioException;
import com.plazoleta.trazabilidad.domain.exception.RestauranteNoEncontradoException;
import com.plazoleta.trazabilidad.domain.model.EficienciaEmpleado;
import com.plazoleta.trazabilidad.domain.model.EficienciaPedido;
import com.plazoleta.trazabilidad.domain.model.RolUsuario;
import com.plazoleta.trazabilidad.domain.util.MensajeError;
import com.plazoleta.trazabilidad.infrastructure.exception.GlobalExceptionHandler;
import com.plazoleta.trazabilidad.infrastructure.exception.ServicioPlazoletaNoDisponibleException;
import com.plazoleta.trazabilidad.infrastructure.input.rest.mapper.EficienciaRestMapper;
import com.plazoleta.trazabilidad.infrastructure.security.UsuarioAutenticado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HU 18 en el controller: el propietario sale del token y los errores del dominio se traducen a HTTP. */
class EficienciaRestControllerTest {

    private static final String BASE = "/api/v1/trazabilidad/restaurantes/3/eficiencia";
    private static final Long ID_PROPIETARIO = 20L;

    private EficienciaHandler handler;
    private MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        handler = mock(EficienciaHandler.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new EficienciaRestController(handler,
                        Mappers.getMapper(EficienciaRestMapper.class)))
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        UsuarioAutenticado propietario =
                new UsuarioAutenticado(ID_PROPIETARIO, "dueno@mail.com", RolUsuario.PROPIETARIO);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                propietario, null, List.of(new SimpleGrantedAuthority("ROLE_PROPIETARIO"))));
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tiempoPorPedido_usaElPropietarioDelTokenYDevuelveLosTiempos() throws Exception {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 16, 10, 0);
        when(handler.consultarTiempoPorPedido(3L, ID_PROPIETARIO)).thenReturn(
                List.of(new EficienciaPedido(15L, 7L, inicio, inicio.plusSeconds(120), 120)));

        mockMvc.perform(get(BASE + "/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPedido").value(15))
                .andExpect(jsonPath("$[0].idEmpleado").value(7))
                .andExpect(jsonPath("$[0].duracionSegundos").value(120));

        verify(handler).consultarTiempoPorPedido(3L, ID_PROPIETARIO);
    }

    @Test
    void ranking_usaElPropietarioDelTokenYDevuelveLasPosiciones() throws Exception {
        when(handler.consultarRankingEmpleados(3L, ID_PROPIETARIO)).thenReturn(
                List.of(new EficienciaEmpleado(1, 8L, 1, 60), new EficienciaEmpleado(2, 7L, 2, 150)));

        mockMvc.perform(get(BASE + "/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].posicion").value(1))
                .andExpect(jsonPath("$[0].idEmpleado").value(8))
                .andExpect(jsonPath("$[1].pedidosEntregados").value(2))
                .andExpect(jsonPath("$[1].tiempoMedioSegundos").value(150));

        verify(handler).consultarRankingEmpleados(3L, ID_PROPIETARIO);
    }

    @Test
    void restauranteAjeno_devuelve403ConElMensajeDelDominio() throws Exception {
        when(handler.consultarTiempoPorPedido(3L, ID_PROPIETARIO))
                .thenThrow(new AccesoDenegadoException(MensajeError.RESTAURANTE_AJENO));

        mockMvc.perform(get(BASE + "/pedidos"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.RESTAURANTE_AJENO))
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void restauranteInexistente_devuelve404() throws Exception {
        when(handler.consultarRankingEmpleados(3L, ID_PROPIETARIO))
                .thenThrow(new RestauranteNoEncontradoException(MensajeError.RESTAURANTE_NO_ENCONTRADO));

        mockMvc.perform(get(BASE + "/empleados"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.RESTAURANTE_NO_ENCONTRADO));
    }

    @Test
    void plazoletaCaida_devuelve503() throws Exception {
        when(handler.consultarTiempoPorPedido(3L, ID_PROPIETARIO)).thenThrow(
                new ServicioPlazoletaNoDisponibleException(MensajeError.PLAZOLETA_NO_DISPONIBLE, null));

        mockMvc.perform(get(BASE + "/pedidos"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.PLAZOLETA_NO_DISPONIBLE));
    }

    @Test
    void idNoNumerico_devuelve400ConElMensajeDelRestaurante() throws Exception {
        mockMvc.perform(get("/api/v1/trazabilidad/restaurantes/abc/eficiencia/pedidos"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.ID_RESTAURANTE_INVALIDO));
    }

    @Test
    void idInvalidoParaElDominio_devuelve400() throws Exception {
        when(handler.consultarTiempoPorPedido(0L, ID_PROPIETARIO))
                .thenThrow(new ReglaDeNegocioException(MensajeError.ID_RESTAURANTE_INVALIDO));

        mockMvc.perform(get("/api/v1/trazabilidad/restaurantes/0/eficiencia/pedidos"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MensajeError.ID_RESTAURANTE_INVALIDO));
    }
}