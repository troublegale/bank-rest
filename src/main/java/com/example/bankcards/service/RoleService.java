package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepo;

    public Role findByName(String name) {
        return roleRepo.findByName(name).orElseThrow(() ->
                new NoSuchElementException(String.format("Role %s not found", name)));
    }

    public Role getUserRole() {
        return findByName("ROLE_USER");
    }


    public Role getAdminRole() {
        return findByName("ROLE_ADMIN");
    }

}
