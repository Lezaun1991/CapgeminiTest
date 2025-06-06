package com.capgemini.test.code.modelo.dto;

import com.capgemini.test.code.modelo.entity.Usuario;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring")
public interface MapperUsuario {
    Usuario toEntity(CrearUsuarioDto crearUsuarioDto);
    DevolverUsuarioCompletoDto toDto(Usuario usuario);
    DevolverUsuarioIdDto toDtoSoloID(Usuario usuario);



}
