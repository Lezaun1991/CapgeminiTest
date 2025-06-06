package com.capgemini.test.code.controller;

import com.capgemini.test.code.modelo.dto.CrearUsuarioDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioCompletoDto;
import com.capgemini.test.code.modelo.dto.DevolverUsuarioIdDto;
import com.capgemini.test.code.service.inter.UsuarioService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService=usuarioService;
    }

    @PostMapping
    public ResponseEntity<DevolverUsuarioIdDto> crearUsuario(@Validated @RequestBody CrearUsuarioDto crearUsuarioDto,
                                                             BindingResult estado){
       if(estado.hasErrors()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
       try{
           return ResponseEntity.ok(usuarioService.guardarUsuario(crearUsuarioDto));
       }
       catch (DataAccessException e){
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
       }


    }
    @GetMapping("/{id}")
    public ResponseEntity<DevolverUsuarioCompletoDto> verUsuario(@PathVariable long id){
        return ResponseEntity.ok(usuarioService.devolverUsuario(id));
    }
}
