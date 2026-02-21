package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.Role;
import java.util.Set;

public record RegisterDTO(String username, String password, Set<Role> roles) {
}