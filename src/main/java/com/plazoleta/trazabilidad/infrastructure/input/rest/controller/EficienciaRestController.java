package com.plazoleta.trazabilidad.infrastructure.input.rest.controller;

import com.plazoleta.trazabilidad.application.handler.EficienciaHandler;
import com.plazoleta.trazabilidad.infrastructure.exception.ErrorResponse;
import com.plazoleta.trazabilidad.infrastructure.input.rest.dto.EficienciaEmpleadoResponseDto;
import com.plazoleta.trazabilidad.infrastructure.input.rest.dto.EficienciaPedidoResponseDto;
import com.plazoleta.trazabilidad.infrastructure.input.rest.mapper.EficienciaRestMapper;
import com.plazoleta.trazabilidad.infrastructure.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.plazoleta.trazabilidad.infrastructure.input.rest.DocumentacionApi.DATOS_INVALIDOS;
import static com.plazoleta.trazabilidad.infrastructure.input.rest.DocumentacionApi.NO_AUTENTICADO;
import static com.plazoleta.trazabilidad.infrastructure.input.rest.DocumentacionApi.NO_ENCONTRADO;
import static com.plazoleta.trazabilidad.infrastructure.input.rest.DocumentacionApi.SIN_PERMISO;

@RestController
@RequestMapping("/api/v1/trazabilidad/restaurantes/{idRestaurante}/eficiencia")
@Tag(name = "Eficiencia", description = "Tiempos de los pedidos de un restaurante, solo para su propietario")
@ApiResponse(responseCode = "401", description = NO_AUTENTICADO,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "403",
        description = SIN_PERMISO + ", o el restaurante pertenece a otro propietario",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "400", description = DATOS_INVALIDOS,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "404", description = NO_ENCONTRADO,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "503", description = "plazoleta-ms no está disponible para verificar el restaurante",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class EficienciaRestController {

    private final EficienciaHandler eficienciaHandler;
    private final EficienciaRestMapper eficienciaRestMapper;

    public EficienciaRestController(EficienciaHandler eficienciaHandler,
                                    EficienciaRestMapper eficienciaRestMapper) {
        this.eficienciaHandler = eficienciaHandler;
        this.eficienciaRestMapper = eficienciaRestMapper;
    }

    @Operation(summary = "Tiempo de cada pedido entregado",
            description = "Solo el PROPIETARIO del restaurante. Por cada pedido ENTREGADO, el tiempo entre que "
                    + "quedó PENDIENTE y que se entregó, en segundos. Los pedidos sin terminar o cancelados no "
                    + "aparecen. El id del propietario sale del token.")
    @GetMapping("/pedidos")
    public ResponseEntity<List<EficienciaPedidoResponseDto>> tiempoPorPedido(
            @Parameter(description = "Id del restaurante", example = "1")
            @PathVariable("idRestaurante") Long idRestaurante,
            @AuthenticationPrincipal UsuarioAutenticado propietario) {
        return ResponseEntity.ok(eficienciaHandler.consultarTiempoPorPedido(idRestaurante, propietario.id())
                .stream().map(eficienciaRestMapper::toPedidoResponse).toList());
    }

    @Operation(summary = "Ranking de empleados por tiempo medio",
            description = "Solo el PROPIETARIO del restaurante. Tiempo medio, en segundos, de los pedidos que "
                    + "entregó cada empleado, del más rápido al más lento (en un empate, el de menor id va "
                    + "primero). El id del propietario sale del token.")
    @GetMapping("/empleados")
    public ResponseEntity<List<EficienciaEmpleadoResponseDto>> rankingEmpleados(
            @Parameter(description = "Id del restaurante", example = "1")
            @PathVariable("idRestaurante") Long idRestaurante,
            @AuthenticationPrincipal UsuarioAutenticado propietario) {
        return ResponseEntity.ok(eficienciaHandler.consultarRankingEmpleados(idRestaurante, propietario.id())
                .stream().map(eficienciaRestMapper::toEmpleadoResponse).toList());
    }
}