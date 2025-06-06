package com.capgemini.test.code.service.impl;

import com.capgemini.test.code.modelo.dto.CrearUsuarioDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioCompletoDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioIdDto;
import com.capgemini.test.code.modelo.dto.MapperUsuario;
import com.capgemini.test.code.modelo.entity.Room;
import com.capgemini.test.code.modelo.entity.Usuario;
import com.capgemini.test.code.modelo.repository.RoomRepository;
import com.capgemini.test.code.modelo.repository.UsuarioRepository;
import com.capgemini.test.code.service.inter.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private UsuarioRepository usuarioRepository;
    private MapperUsuario mapperUsuario;
    private RoomRepository roomRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              MapperUsuario mapperUsuario,
                              RoomRepository roomRepository){
        this.usuarioRepository=usuarioRepository;
        this.mapperUsuario=mapperUsuario;
        this.roomRepository = roomRepository;
    }


    @Override
    public DevolverUsuarioIdDto guardarUsuario(CrearUsuarioDto crearUsuarioDto) {
        Usuario usuarioNuevo = mapperUsuario.toEntity(crearUsuarioDto);

        if (usuarioRepository.findByEmail(usuarioNuevo.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está en uso");
        }


        Room room = roomRepository.findById(1L).orElseThrow();
        usuarioNuevo.setRoom(room);
        usuarioRepository.save(usuarioNuevo);
        return mapperUsuario.toDtoSoloID(usuarioNuevo);
    }

    @Override
    public DevolverUsuarioCompletoDto devolverUsuario(Long id) {
        Usuario usuarioEncontrado = usuarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (usuarioEncontrado.getRoom() == null || usuarioEncontrado.getRoom().getId() != 1L) {
            throw new IllegalStateException("El usuario no pertenece a la sala permitida");
        }
        return mapperUsuario.toDto(usuarioEncontrado);

    }
}
