package com.plazoleta.trazabilidad.domain.util;

public final class MensajeError {

    public static final String TOKEN_REQUERIDO = "Debes iniciar sesión con un token válido";
    public static final String SIN_PERMISO = "Tu rol no tiene permiso para esta acción";

    public static final String PRIMER_LOG_DEBE_SER_PENDIENTE =
            "El primer registro de un pedido debe tener el estado PENDIENTE";
    public static final String ESTADO_REPETIDO = "El estado nuevo debe ser distinto al estado anterior";
        public static final String PEDIDO_SIN_REGISTROS = "El pedido no tiene registros de trazabilidad";
    public static final String PEDIDO_AJENO = "El pedido no pertenece al cliente autenticado";
    public static final String RESTAURANTE_AJENO = "El restaurante no pertenece al propietario autenticado";
    public static final String RESTAURANTE_NO_ENCONTRADO = "No existe un restaurante con ese id";
        public static final String PLAZOLETA_NO_DISPONIBLE =
            "El servicio de plazoleta no está disponible, intenta más tarde";

    public static final String ID_PEDIDO_INVALIDO = "El idPedido es obligatorio y debe ser un número positivo";
    public static final String ID_CLIENTE_INVALIDO = "El idCliente es obligatorio y debe ser un número positivo";
    public static final String ID_RESTAURANTE_INVALIDO =
            "El idRestaurante es obligatorio y debe ser un número positivo";
    public static final String ID_EMPLEADO_INVALIDO = "El idEmpleado debe ser un número positivo";
    public static final String ESTADO_NUEVO_OBLIGATORIO = "El estadoNuevo es obligatorio";
    public static final String CUERPO_INVALIDO =
            "El cuerpo de la petición no es un JSON válido o trae un estado desconocido";

    private MensajeError() {
    }
}