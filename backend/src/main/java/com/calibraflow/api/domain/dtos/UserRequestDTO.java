package com.calibraflow.api.application.dtos;

import com.calibraflow.api.domain.entities.enums.UserRole;
import com.calibraflow.api.infrastructure.validations.Cpf;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(

        @NotBlank(message = "O nome nao pode ser vazio")
        String name,

        @NotBlank(message = "O e-mail nao pode ser vazio")
        @Email(message = "Formato de e-mail invalido")
        String email,

        @NotBlank(message = "A senha nao pode ser vazia")
        @Size(min = 6, message = "A senha deve ter no minimo 6 caracteres")
        String password,

        @NotBlank(message = "O CPF e obrigatorio")
        @Cpf(message = "O CPF informado e invalido matematicamente")
        String cpf,

        @NotNull(message = "O perfil de acesso e obrigatorio")
        UserRole role

) {}