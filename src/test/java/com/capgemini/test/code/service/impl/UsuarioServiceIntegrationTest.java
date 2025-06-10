package com.capgemini.test.code.service.impl;



import com.capgemini.test.code.modelo.dto.CrearUsuarioDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioCompletoDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioIdDto;
import com.capgemini.test.code.modelo.dto.MapperUsuario;
import com.capgemini.test.code.modelo.entity.Room;
import com.capgemini.test.code.modelo.entity.Usuario;
import com.capgemini.test.code.modelo.repository.RoomRepository;
import com.capgemini.test.code.modelo.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional // Restaura todo al terminar el test
public class UsuarioServiceIntegrationTest {


    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeAll
    void setUp() {
        Room sala1 = new Room();
        sala1.setName("Room 1");
        roomRepository.save(sala1);
    }


    @Test
    void deberiaGuardarUsuarioCorrectamente() {

        //CREAMOS UN CREARUSUARIODTO
        CrearUsuarioDto dto = new CrearUsuarioDto();
        dto.setDni("99999998A");
        dto.setEmail("test@example.com");
        dto.setPhone("600123123");
        dto.setName("Víctor");
        dto.setRole("admin");


        // Act: ejecutamos el método real
        DevolverUsuarioIdDto devolverUsuarioIdDto =  usuarioService.guardarUsuario(dto);

        // Assert: comprobamos que se guardó
        assertNotNull(usuarioRepository.findById(devolverUsuarioIdDto.getId()));
        Optional<Usuario> guardado = usuarioRepository.findById(devolverUsuarioIdDto.getId());
        assertTrue(guardado.isPresent());
        assertEquals("Víctor", guardado.get().getName());
    }

    @Test
    void deberiaDevolverUsuarioCorrectamenteSiPerteneceASala1() {


        // Crear usuario en sala 1
        Usuario usuario = new Usuario();
        usuario.setDni("11111111A");
        usuario.setEmail("correcto@test.com");
        usuario.setName("Correcto");
        usuario.setPhone("600111111");
        usuario.setRole("admin");
        Room room = roomRepository.findById(1L).orElseThrow();
        usuario.setRoom(room);
        usuarioRepository.save(usuario);

        DevolverUsuarioCompletoDto resultado = usuarioService.devolverUsuario(usuario.getId());

        assertNotNull(resultado);
        assertEquals("Correcto", resultado.getName());
        assertEquals("correcto@test.com", resultado.getEmail());
    }


    @Test
    void deberiaLanzarNotFoundSiElUsuarioNoExiste() {
        Long idInexistente = 9999L;

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.devolverUsuario(idInexistente);
        });

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Usuario no encontrado", ex.getReason());
    }

    @Test
    void deberiaLanzarExcepcionSiElUsuarioNoPerteneceASala1() {
        // Crear sala 2
        Room sala2 = new Room();
        sala2.setName("Sala 2");
        roomRepository.save(sala2);

        // Crear usuario asignado a sala 2
        Usuario usuario = new Usuario();
        usuario.setDni("12345678A");
        usuario.setEmail("ejemplo@test.com");
        usuario.setName("Usuario Test");
        usuario.setPhone("600000000");
        usuario.setRole("user");
        usuario.setRoom(sala2);
        usuarioRepository.save(usuario);

        assertThrows(IllegalStateException.class, () -> {
            usuarioService.devolverUsuario(usuario.getId());
        });
    }


    @Test
    void deberiaLanzarExcepcionSiElEmailYaExiste() {
        // Arrange: crear primer usuario
        CrearUsuarioDto dto = new CrearUsuarioDto();
        dto.setDni("99999999A");
        dto.setEmail("test@correo.com");
        dto.setPhone("600000000");
        dto.setName("Usuario 1");
        dto.setRole("admin");

        usuarioService.guardarUsuario(dto); // primer guardado OK

        // Act & Assert: el segundo debería fallar
        CrearUsuarioDto duplicado = new CrearUsuarioDto();
        duplicado.setDni("88888888B");
        duplicado.setEmail("test@correo.com"); // mismo email
        duplicado.setPhone("699999999");
        duplicado.setName("Usuario duplicado");
        duplicado.setRole("admin");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.guardarUsuario(duplicado);
        });

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode()); // ✅ Verifica el código
        assertEquals("El email ya está en uso", ex.getReason()); // ✅ Verifica el mensaje
    }

    @Test
    void deberiaLanzarExcepcionSiElDniNoEsValido() {
        // Arrange: crear primer usuario
        CrearUsuarioDto dto = new CrearUsuarioDto();
        dto.setDni("99999999w");
        dto.setEmail("test@correo.com");
        dto.setPhone("600000000");
        dto.setName("Usuario 1");
        dto.setRole("admin");




        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.guardarUsuario(dto);
        });

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode()); // ✅ Verifica el código
        assertEquals("El DNI no es válido", ex.getReason()); // ✅ Verifica el mensaje
    }

}
