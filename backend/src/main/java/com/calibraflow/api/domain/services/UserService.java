package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.dtos.UserResponseDTO;
import com.calibraflow.api.domain.dtos.UserUpdatePermissionsDTO;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findOptionalByEmail(email);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(u -> UserResponseDTO.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .cpf(u.getCpf())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .build());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return UserResponseDTO.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .cpf(u.getCpf())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .build();
    }

    @Transactional
    public UserResponseDTO updatePermissions(Long id, UserUpdatePermissionsDTO dto) {
        User u = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        u.setPermissions(dto.permissions().stream()
                .map(Enum::name)
                .collect(Collectors.toSet()));

        userRepository.save(u);
        return UserResponseDTO.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .cpf(u.getCpf())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .build();
    }

    @Transactional
    public void softDelete(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        u.setEnabled(false);
        userRepository.save(u);
    }
}