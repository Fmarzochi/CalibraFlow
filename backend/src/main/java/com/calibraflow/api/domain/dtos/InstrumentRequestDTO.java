package com.calibraflow.api.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InstrumentRequestDTO(

        @NotBlank(message = "A TAG do instrumento e obrigatoria")
        @Size(max = 100, message = "A TAG deve ter no maximo 100 caracteres")
        String tag,

        @NotBlank(message = "O nome do instrumento e obrigatorio")
        @Size(max = 255, message = "O nome deve ter no maximo 255 caracteres")
        String name,

        String serialNumber,

        String manufacturer,

        String model,

        String location,

        String tolerance
) {}