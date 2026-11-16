package com.plazoleta.trazabilidad.infrastructure.input.rest.controller;

import com.plazoleta.trazabilidad.application.handler.LogPedidoHandler;
import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.infrastructure.exception.ErrorResponse;
import com.plazoleta.trazabilidad.infrastructure.input.rest.dto.LogPedidoRequestDto;
import com.plazoleta.trazabilidad.infrastructure.input.rest.dto.LogPedidoResponseDto;
import com.plazoleta.trazabilidad.infrastructure.input.rest.mapper.LogPedidoRestMapper;
import com.plazoleta.trazabilidad.infrastructure.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.plazoleta.trazabilidad.infrastructure.input.rest.DocumentacionApi.DATOS_INVALIDOS;
import static com.plazoleta.trazabilidad.infrastructure.input.rest.DocumentacionApi.NO_AUTENTICADO;
import static com.plazoleta.trazabilidad.infrastructure.input.rest.DocumentacionApi.NO_ENCONTRADO;
import static com.plazoleta.trazabilidad.infrastructure.input.rest.DocumentacionApi.SIN_PERMISO;

@RestController
@RequestMapping("/api/v1/trazabilidad")
@Tag(name = "Trazabilidad", description = "Registro e historial de los cambios de estado de los pedidos")
@ApiResponse(responseCode = "401", description = NO_AUTENTICADO,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class TrazabilidadRestController {

    private final LogPedidoHandler logPedidoHandler;
    private final LogPedidoRestMapper logPedidoRestMapper;

    public TrazabilidadRestController(LogPedidoHandler logPedidoHandler,
                                      LogPedidoRestMapper logPedidoRestMapper) {
        this.logPedidoHandler = logPedidoHandler;
        this.logPedidoRestMapper = logPedidoRestMapper;
    }

    @Operation(summary = "Registrar un cambio de estado de un pedido",
            description = "Lo llama plazoleta-ms cada vez que un pedido cambia de estado, con el token que "
                    + "recibió: CLIENTE (Pendiente y Cancelado) o EMPLEADO (los demás cambios). El primer "
                    + "registro de un pedido debe ser PENDIENTE sin estado anterior, y el estado nuevo debe "
                    + "ser distinto del anterior. La fecha la pone el servicio.")
    @ApiResponse(responseCode = "400", description = DATOS_INVALIDOS,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = SIN_PERMISO,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<LogPedidoResponseDto> registrarLog(@Valid @RequestBody LogPedidoRequestDto request) {
        LogPedido registrado = logPedidoHandler.registrarLog(logPedidoRestMapper.toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(logPedidoRestMapper.toResponse(registrado));
    }

    @Operation(summary = "Consultar el historial de un pedido",
            description = "Devuelve los cambios de estado del pedido, del más antiguo al más reciente. Solo "
                    + "el CLIENTE dueño del pedido puede consultarlo: su id sale del token, no de un parámetro.")
    @ApiResponse(responseCode = "400", description = DATOS_INVALIDOS,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403",
            description = SIN_PERMISO + ", o el pedido pertenece a otro cliente",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = NO_ENCONTRADO,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{idPedido}")
    public ResponseEntity<List<LogPedidoResponseDto>> consultarHistorial(
            @PathVariable Long idPedido,
            @AuthenticationPrincipal UsuarioAutenticado cliente) {
        List<LogPedido> historial = logPedidoHandler.consultarHistorial(idPedido, cliente.id());
        return ResponseEntity.ok(historial.stream().map(logPedidoRestMapper::toResponse).toList());
    }
}