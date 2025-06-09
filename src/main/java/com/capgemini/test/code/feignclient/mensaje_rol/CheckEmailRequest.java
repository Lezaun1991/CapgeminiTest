package com.capgemini.test.code.feignclient.mensaje_rol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckEmailRequest {
    private String email;
    private String message;
}
