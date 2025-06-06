package com.capgemini.test.code.modelo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevolverUsuarioCompletoDto {

    private Long id;
    @NotBlank
    @Size(max = 6)
    private String name;

    @NotBlank
    @Size(max = 150)
    @Email
    private String email;

    @NotBlank
    @Size(max = 15)
    private String dni;

    @NotBlank
    @Size(max = 15)
    private String phone;

    @Pattern(regexp = "admin|superadmin", message = "El rol debe ser admin o superadmin")
    private String role;


}
