package com.capgemini.test.code.feignclient.mensaje_rol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckSmsRequest {
    private String phone;
    private String message;
}
