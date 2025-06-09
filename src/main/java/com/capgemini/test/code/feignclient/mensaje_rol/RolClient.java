package com.capgemini.test.code.feignclient.mensaje_rol;

import com.capgemini.test.code.feignclient.clients.DniClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "checkRolClient", url = "${external.service.url}", configuration = DniClient.FeignConfig.class)
public interface RolClient {
    @PostMapping("/email")
    ResponseEntity<CheckRolResponse> mandarCorreo(@RequestBody CheckEmailRequest checkEmailRequest);
    @PostMapping("/sms")
    ResponseEntity<CheckRolResponse> mandarSms(@RequestBody CheckSmsRequest checkSmsRequest);

}
