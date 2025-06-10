package com.capgemini.test.code.service.impl;

import com.capgemini.test.code.feignclient.clients.CheckDniRequest;
import com.capgemini.test.code.feignclient.clients.CheckDniResponse;
import com.capgemini.test.code.feignclient.clients.DniClient;
import com.capgemini.test.code.feignclient.mensaje_rol.CheckEmailRequest;
import com.capgemini.test.code.feignclient.mensaje_rol.RolClient;
import com.capgemini.test.code.modelo.dto.CrearUsuarioDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioCompletoDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioIdDto;
import com.capgemini.test.code.modelo.dto.MapperUsuario;
import com.capgemini.test.code.modelo.entity.Room;
import com.capgemini.test.code.modelo.entity.Usuario;
import com.capgemini.test.code.modelo.repository.RoomRepository;
import com.capgemini.test.code.modelo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private MapperUsuario mapperUsuario;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private DniClient dniClient;
    @Mock
    private RolClient rolClient;

    @InjectMocks
    private UsuarioServiceImpl usuarioServiceImpl;


    @Test
    void guardarUsuario_deberiaGuardarCorrectamenteYDevolverId() {
        //CREAMOS UN CREARUSUARIODTO
        CrearUsuarioDto dto = new CrearUsuarioDto();
        dto.setDni("12345678A");
        dto.setEmail("test@example.com");
        dto.setPhone("600123123");
        dto.setName("Víctor");
        dto.setRole("admin");

        //CREAMOS UN USUARIO Y LE PASAMOS LOS DATOS DEL DTO ESCRITO ARRIBA
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setPhone(dto.getPhone());
        usuario.setName(dto.getName());
        usuario.setRole(dto.getRole());

        //CREAMOS Y ASIGNAMOS UN ID A LA ROOM
        Room room = new Room();
        room.setId(1L);

        //CREAMOS LA RESPUESTA DTO QUE VAMOS A REALIZAR OSEA EL RESULTADO, QUE EN ESTE CASO ES SIMPLEMENTE EL ID
        DevolverUsuarioIdDto devolverDto = new DevolverUsuarioIdDto();
        devolverDto.setId(1L);

        //AHORA SIMULAMOS A TRAVES DE MOCK TODOS LOS PROCESOS QUE PASA NUESTRO METODO
        // Y LAS DIFERENTES RESPUESTAS QUE RECIBE
        when(dniClient.check(any(CheckDniRequest.class)))
                .thenReturn(ResponseEntity.ok(new CheckDniResponse("Valid DNI")));

        when(mapperUsuario.toEntity(dto)).thenReturn(usuario);
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(mapperUsuario.toDtoSoloID(usuario)).thenReturn(devolverDto);

        //AQUI EFECTUAMOS EL METODO QUE ESTAMOS COMPROBANDO Y PONEMOS EN MARCHA TODOS LOS WHEN DE ARRIBA
        //Y GUARDAMOS EL RESULTADO PARA LAS DIFERENTES COMPROBACIONES
        DevolverUsuarioIdDto resultado = usuarioServiceImpl.guardarUsuario(dto);


        //AQUI REALIZAMOS LAS COMPROBACIONES DEL RESULTADO Y VEMOS SI NOS HA DADO LO QUE DEBIA
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId()); // Verificas el valor devuelto

        //VERIFICAMOS SI HA PASADO POR LOS DIFERENTES METODOS QUE TENIA QUE PASAR.
        verify(usuarioRepository).save(any(Usuario.class));
        verify(rolClient).mandarCorreo(any(CheckEmailRequest.class));
        verify(usuarioRepository).findByEmail("test@example.com");
    }

    @Test
    void devolverUsuario_devuelveDtoSiUsuarioExisteYSalaEs1() {
        //SELECCIONAMOS ID
        Long id = 1L;

        // CREAMOS ROOM CON ID
        Room room = new Room();
        room.setId(1L);

        // CREAMOS EL USUARIO Y LE ASIGNAMOS EL ID Y EL ROOM
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setRoom(room);
        usuario.setName("Víctor");

        // CREAMOS EL DTO QUE VAMOS A DEVOLVER
        DevolverUsuarioCompletoDto dtoEsperado = new DevolverUsuarioCompletoDto();
        dtoEsperado.setId(id);
        dtoEsperado.setName("Víctor");

        //REALIZAMOS LOS MOCKS
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(mapperUsuario.toDto(usuario)).thenReturn(dtoEsperado);

        //REALIZAMOS EL METODO Y LO GUARDAMOS A UN RESULTADO
        DevolverUsuarioCompletoDto resultado = usuarioServiceImpl.devolverUsuario(id);

        //REALIZAMOS LAS COMPROBACIONES Y VERIFICACIONES
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Víctor", resultado.getName());


        verify(usuarioRepository).findById(id);
        verify(mapperUsuario).toDto(usuario);
    }

    @Test
    void guardarUsuario_lanzaExceptionSiDniEsInvalido() {
        //CREAMOS UN CREARUSUARIODTO
        CrearUsuarioDto dto = new CrearUsuarioDto();
        dto.setDni("12345678Z");
        dto.setEmail("nuevo@email.com");
        dto.setPhone("666666666");
        dto.setRole("admin");
        dto.setName("Nombre");

        //REALIZAMOS LOS DIFERENTES WHEN POR DONDE VA A PASAR ANTES DE LANZAR LA EXCEPCION
        when(dniClient.check(any(CheckDniRequest.class)))
                .thenReturn(ResponseEntity.ok(new CheckDniResponse("Invalid DNI")));

        //REALIZAMOS EL METODO QUE PROVOCA LA EXCEPCION
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            usuarioServiceImpl.guardarUsuario(dto);
        });

        //COMPROBAMOS EL RESULTADO Y VALIDAMOS
        assertEquals("409 CONFLICT \"El DNI no es válido\"", ex.getMessage());
    }
    @Test
    void guardarUsuario_lanzaExcepcionEmailDuplicado(){
        //CREAMOS UN CREARUSUARIODTO
        CrearUsuarioDto dto = new CrearUsuarioDto();
        dto.setDni("12345678Z");
        dto.setEmail("duplicado@email.com");
        dto.setPhone("666666666");
        dto.setRole("admin");
        dto.setName("Nombre");

        //LO PASAMOS A USUARIO SIMULANDO USAR EL MAPSTRUCT
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setPhone(dto.getPhone());
        usuario.setName(dto.getName());
        usuario.setRole(dto.getRole());

        //REALIZAMOS LA VALIDACION DE QUE EL DNI ES CORRECTO PARA QUE NO SE CORTE ANTES LA EJECUCION
        when(dniClient.check(any(CheckDniRequest.class)))
                .thenReturn(ResponseEntity.ok(new CheckDniResponse("Valid DNI")));

        //SIMULAMOS QUE YA EXISTE UN CLIENTE CON ESTE EMAIL
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail("duplicado@email.com");

        //MOCKEAMOS LOS METODOS QUE EJECUTA HASTA QUE LANZA LA EXCEPCION
        when(mapperUsuario.toEntity(dto)).thenReturn(usuario);
        when(usuarioRepository.findByEmail("duplicado@email.com")).thenReturn(Optional.of(usuarioExistente));


        //REALIZAMOS EL METODO QUE PROVOCA LA EXCEPCION
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            usuarioServiceImpl.guardarUsuario(dto);
        });

        // //COMPROBAMOS EL RESULTADO Y VALIDAMOS
        assertEquals("409 CONFLICT \"El email ya está en uso\"", ex.getMessage());

        verify(dniClient).check(any(CheckDniRequest.class));
        verify(mapperUsuario).toEntity(dto);
    }

    @Test
    void devolverUsuario_lanzaExceptionSiUsuarioNoExiste() {
        // ASIGNAMOS UN ID
        Long id = 99L;

        //REALIZAMOS EL MOCK QUE NOS DEVUELVE VACIO
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        //REALIZAMOS EL METODO QUE PROVOCA LA EXCEPCION
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            usuarioServiceImpl.devolverUsuario(id);
        });

        //REALIZAMOS LAS COMPROBACIONES Y VERIFICACIONES
        assertEquals("404 NOT_FOUND \"Usuario no encontrado\"", ex.getMessage());

        verify(usuarioRepository).findById(id);
    }

    @Test
    void devolverUsuario_lanzaExceptionSiRoomEsNull() {
        // CREAMOS UN ID
        Long id = 1L;

        //ASIGNAMOS ESE ID A UN USUARIO Y UN NULL AL ROOM
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(id);
        usuarioMock.setRoom(null);

        //MOCKEAMOS QUE LO BUSCAMOS POR ID Y NOS LO DEVUELVE
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioMock));

        ////REALIZAMOS EL METODO QUE PROVOCA LA EXCEPCION
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            usuarioServiceImpl.devolverUsuario(id);
        });

        //COMPROBAMOS EL RESULTADO Y VALIDAMOS
        assertEquals("El usuario no pertenece a la sala permitida o es nulo", ex.getMessage());

        verify(usuarioRepository).findById(id);
    }

    @Test
    void devolverUsuario_lanzaException_siRoomTieneOtroId() {
        //ID INICIAL USUARIO
        Long id = 1L;

        //CREAMOS ROOM CON ID DISTINTIO DE 1
        Room room = new Room();
        room.setId(2L);

        //CREAMOS USUARIO CON ESA SALA
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setRoom(room);

        //SIMULAMOS QUE ENCUENTRA AL USUARIO
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        //REALIZAMOS EL METODO QUE PROVOCA LA EXCEPCION
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            usuarioServiceImpl.devolverUsuario(id);
        });

        //COMPROBAMOS EL RESULTADO Y VALIDAMOS
        assertEquals("El usuario no pertenece a la sala permitida o es nulo", ex.getMessage());

        verify(usuarioRepository).findById(id);
    }
}
