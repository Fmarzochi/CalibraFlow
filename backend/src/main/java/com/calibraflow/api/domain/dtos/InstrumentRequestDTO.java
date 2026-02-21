package com.calibraflow.api.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InstrumentRequestDTO {

    @NotBlank(message = "A tag é obrigatória")
    private String tag;

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    private String manufacturer;
    private String model;
    private String serialNumber;
    private String range;
    private String tolerance;
    private String resolution;
    private String patrimonyCode;

    @NotNull(message = "O ID da categoria é obrigatório")
    private Long categoryId;

    @NotNull(message = "O ID da localização é obrigatório")
    private Long locationId;

    private Long periodicityId;
}