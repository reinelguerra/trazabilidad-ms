package com.plazoleta.trazabilidad.infrastructure.config;

import com.plazoleta.trazabilidad.application.handler.EficienciaHandler;
import com.plazoleta.trazabilidad.domain.model.RolUsuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Quién puede consultar la eficiencia (HU 18): solo el PROPIETARIO. */
@SpringBootTest(properties = "jwt.secret=clave-solo-para-pruebas-0123456789-abcdefghij")
@AutoConfigureMockMvc
class SeguridadEficienciaTest {

    private static final String SECRETO = "clave-solo-para-pruebas-0123456789-abcdefghij";
    private static final String PEDIDOS = "/api/v1/trazabilidad/restaurantes/3/eficiencia/pedidos";
    private static final String EMPLEADOS = "/api/v1/trazabilidad/restaurantes/3/eficiencia/empleados";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EficienciaHandler eficienciaHandler;

    private String bearer(RolUsuario rol) {
        String token = Jwts.builder()
                .claim("id", 20L)
                .claim("rol", rol.name())
                .claim("correo", "test@mail.com")
                .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(Keys.hmacShaKeyFor(SECRETO.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();
        return "Bearer " + token;
    }

    @Test
    void pedidos_sinToken_devuelve401() throws Exception {
        mockMvc.perform(get(PEDIDOS)).andExpect(status().isUnauthorized());
    }

    @Test
    void pedidos_propietario_puede() throws Exception {
        mockMvc.perform(get(PEDIDOS).header("Authorization", bearer(RolUsuario.PROPIETARIO)))
                .andExpect(status().isOk());
    }

    @Test
    void pedidos_cliente_noPuede() throws Exception {
        mockMvc.perform(get(PEDIDOS).header("Authorization", bearer(RolUsuario.CLIENTE)))
                .andExpect(status().isForbidden());
    }

    @Test
    void pedidos_empleado_noPuede() throws Exception {
        mockMvc.perform(get(PEDIDOS).header("Authorization", bearer(RolUsuario.EMPLEADO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void pedidos_administrador_noPuede() throws Exception {
        mockMvc.perform(get(PEDIDOS).header("Authorization", bearer(RolUsuario.ADMINISTRADOR)))
                .andExpect(status().isForbidden());
    }

    @Test
    void empleados_propietario_puede() throws Exception {
        mockMvc.perform(get(EMPLEADOS).header("Authorization", bearer(RolUsuario.PROPIETARIO)))
                .andExpect(status().isOk());
    }

    @Test
    void empleados_cliente_noPuede() throws Exception {
        mockMvc.perform(get(EMPLEADOS).header("Authorization", bearer(RolUsuario.CLIENTE)))
                .andExpect(status().isForbidden());
    }
}