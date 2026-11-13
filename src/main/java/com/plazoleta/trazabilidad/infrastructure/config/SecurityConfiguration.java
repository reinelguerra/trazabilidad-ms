package com.plazoleta.trazabilidad.infrastructure.config;

import com.plazoleta.trazabilidad.domain.model.RolUsuario;
import com.plazoleta.trazabilidad.domain.util.MensajeError;
import com.plazoleta.trazabilidad.infrastructure.security.JwtAuthenticationFilter;
import com.plazoleta.trazabilidad.infrastructure.security.JwtTokenParser;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfiguration {

    private static final String RUTA_TRAZABILIDAD = "/api/v1/trazabilidad";

    @Bean
    public JwtTokenParser jwtTokenParser(@Value("${jwt.secret}") String secreto) {
        return new JwtTokenParser(secreto);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenParser jwtTokenParser)
            throws Exception {
        PathPatternRequestMatcher.Builder rutas = PathPatternRequestMatcher.withDefaults();
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(cabeceras -> cabeceras.frameOptions(marco -> marco.sameOrigin()))
                .authorizeHttpRequests(peticiones -> peticiones
                        .requestMatchers(
                                rutas.matcher("/swagger-ui/**"),
                                rutas.matcher("/swagger-ui.html"),
                                rutas.matcher("/v3/api-docs/**"),
                                rutas.matcher("/h2-console/**")).permitAll()
                        .requestMatchers(rutas.matcher(HttpMethod.POST, RUTA_TRAZABILIDAD))
                                .hasAnyRole(RolUsuario.CLIENTE.name(), RolUsuario.EMPLEADO.name())
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenParser),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint((request, response, ex) -> escribirError(response,
                                HttpServletResponse.SC_UNAUTHORIZED, MensajeError.TOKEN_REQUERIDO))
                        .accessDeniedHandler((request, response, ex) -> escribirError(response,
                                HttpServletResponse.SC_FORBIDDEN, MensajeError.SIN_PERMISO)));
        return http.build();
    }

    private static void escribirError(HttpServletResponse response, int status, String mensaje)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"mensaje\":\"" + mensaje + "\",\"status\":" + status + "}");
    }
}