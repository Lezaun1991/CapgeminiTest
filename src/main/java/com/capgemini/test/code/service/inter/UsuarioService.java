package com.capgemini.test.code.service.inter;

import com.capgemini.test.code.modelo.dto.CrearUsuarioDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioCompletoDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioIdDto;

public interface UsuarioService {
    DevolverUsuarioIdDto guardarUsuario(CrearUsuarioDto crearUsuarioDto);
    DevolverUsuarioCompletoDto devolverUsuario(Long id);

}
