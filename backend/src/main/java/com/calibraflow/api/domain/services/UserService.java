package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.dtos.UserResponseDTO;
import com.calibraflow.api.domain.dtos.UserUpdatePermissionsDTO;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado."));
        return mapToResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updatePermissions(Long id, UserUpdatePermissionsDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado."));

        user.setPermissions(dto.permissions());
        userRepository.save(user);

        return mapToResponseDTO(user);
    }

    @Transactional
    public void softDelete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado."));

        user.setEnabled(false);
        userRepository.save(user);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getRole(),
                user.isEnabled(),
                user.getPermissions()
        );
    }
}