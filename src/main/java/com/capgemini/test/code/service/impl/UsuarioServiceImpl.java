package com.capgemini.test.code.service.impl;

import com.capgemini.test.code.feignclient.clients.CheckDniRequest;
import com.capgemini.test.code.feignclient.clients.CheckDniResponse;
import com.capgemini.test.code.feignclient.clients.DniClient;
import com.capgemini.test.code.feignclient.clients.mensaje_rol.CheckEmailRequest;
import com.capgemini.test.code.feignclient.clients.mensaje_rol.CheckSmsRequest;
import com.capgemini.test.code.feignclient.clients.mensaje_rol.RolClient;
import com.capgemini.test.code.modelo.dto.CrearUsuarioDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioCompletoDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioIdDto;
import com.capgemini.test.code.modelo.dto.MapperUsuario;
import com.capgemini.test.code.modelo.entity.Room;
import com.capgemini.test.code.modelo.entity.Usuario;
import com.capgemini.test.code.modelo.repository.RoomRepository;
import com.capgemini.test.code.modelo.repository.UsuarioRepository;
import com.capgemini.test.code.service.inter.UsuarioService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private UsuarioRepository usuarioRepository;
    private MapperUsuario mapperUsuario;
    private RoomRepository roomRepository;
    //private ValidadorDni validadorDni; ESTO NO SIRVE AQUI PORQUE USAMOS FEIGN
    private DniClient dniClient;
    private RolClient rolClient;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              MapperUsuario mapperUsuario,
                              RoomRepository roomRepository,
                              DniClient dniClient,
                              RolClient rolClient){
        this.usuarioRepository=usuarioRepository;
        this.mapperUsuario=mapperUsuario;
        this.roomRepository = roomRepository;
        this.dniClient = dniClient;
        this.rolClient = rolClient;
    }


    @Override
    @Transactional
    public DevolverUsuarioIdDto guardarUsuario(CrearUsuarioDto crearUsuarioDto) {

        validarDni(crearUsuarioDto.getDni());

        Usuario usuarioNuevo = convertirDtoAEntidad(crearUsuarioDto);

        validarEmailDuplicado(usuarioNuevo.getEmail());

        asignarRoom(usuarioNuevo);

        usuarioRepository.save(usuarioNuevo);

        mandarMensajeSegunRol(usuarioNuevo);

        return mapperUsuario.toDtoSoloID(usuarioNuevo);
    }

    @Override
    public DevolverUsuarioCompletoDto devolverUsuario(Long id) {
        Usuario usuarioEncontrado = usuarioRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (usuarioEncontrado.getRoom() == null || usuarioEncontrado.getRoom().getId() != 1L) {
            throw new IllegalStateException("El usuario no pertenece a la sala permitida o es nulo");
        }
        return mapperUsuario.toDto(usuarioEncontrado);

    }

    private void validarDni(String dni){
        try{
            CheckDniResponse response = dniClient.check(new CheckDniRequest(dni)).getBody();
            if (!response.getMessage().equalsIgnoreCase("Valid DNI")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,"El DNI no es válido");
            }
        }
        catch (FeignException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El DNI no es válido");
        }
    }

    private Usuario convertirDtoAEntidad(CrearUsuarioDto dto){
        return mapperUsuario.toEntity(dto);
    }

    private void validarEmailDuplicado(String email){
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"El email ya está en uso");
        }
    }
    private void asignarRoom(Usuario usuario){
        Room room = roomRepository.findById(1L).orElseThrow();
        usuario.setRoom(room);
    }
    private void mandarMensajeSegunRol(Usuario usuario){
        if(usuario.getRole().equals("admin")) rolClient.mandarCorreo(new CheckEmailRequest(usuario.getEmail(),"usuario guardado"));
        if(usuario.getRole().equals("superadmin")) rolClient.mandarSms(new CheckSmsRequest(usuario.getPhone(),"usuario guardado"));
    }
}
