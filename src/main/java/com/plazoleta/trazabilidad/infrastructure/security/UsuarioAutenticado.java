package com.plazoleta.trazabilidad.infrastructure.security;

import com.plazoleta.trazabilidad.domain.model.RolUsuario;

public record UsuarioAutenticado(Long id, String correo, RolUsuario rol) {
}