package com.plazoleta.trazabilidad.infrastructure.exception;

import com.plazoleta.trazabilidad.domain.exception.AccesoDenegadoException;
import com.plazoleta.trazabilidad.domain.exception.DomainException;
import com.plazoleta.trazabilidad.domain.exception.PedidoSinRegistrosException;
import com.plazoleta.trazabilidad.domain.util.MensajeError;
import com.plazoleta.trazabilidad.domain.exception.RestauranteNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> reglaDeNegocio(DomainException e) {
        return construir(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(PedidoSinRegistrosException.class)
    public ResponseEntity<ErrorResponse> pedidoSinRegistros(PedidoSinRegistrosException e) {
        return construir(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorResponse> accesoDenegado(AccesoDenegadoException e) {
        return construir(HttpStatus.FORBIDDEN, e.getMessage());
    }
        @ExceptionHandler(RestauranteNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> restauranteNoEncontrado(RestauranteNoEncontradoException e) {
        return construir(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ServicioPlazoletaNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> plazoletaNoDisponible(ServicioPlazoletaNoDisponibleException e) {
        return construir(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> datosInvalidos(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .distinct()
                .sorted()
                .collect(Collectors.joining("; "));
        return construir(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException e) {
        return construir(HttpStatus.BAD_REQUEST, MensajeError.CUERPO_INVALIDO);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> idInvalido(MethodArgumentTypeMismatchException e) {
        String mensaje = "idRestaurante".equals(e.getName())
                ? MensajeError.ID_RESTAURANTE_INVALIDO
                : MensajeError.ID_PEDIDO_INVALIDO;
        return construir(HttpStatus.BAD_REQUEST, mensaje);
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(new ErrorResponse(mensaje, status.value()));
    }
}