package com.example.bankcards.security;

import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EmailTakenException;
import com.example.bankcards.exception.InvalidSearchQueryException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getByEmail(username);
    }

    @Transactional
    public User getByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User %s not found", email)));
    }

    @Transactional
    public Boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Transactional
    public User createUser(RegistrationRequest request) {
        String email = request.email();
        if (userRepo.existsByEmail(email)) {
            throw new EmailTakenException(String.format("Email %s is already taken", email));
        }
        Role userRole = roleService.getUserRole();
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .roles(List.of(userRole))
                .build();
        return userRepo.save(user);
    }

    @Transactional
    public void createUser(User user) {
        userRepo.save(user);
    }

    @Transactional
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByEmail(email);
    }

    @Transactional
    public User getById(Long id) {
        return userRepo.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("User with id %d not found", id)));
    }

    @Transactional
    public void deleteById(Long id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
        } else {
            throw new NoSuchElementException(String.format("User with id %d not found", id));
        }
    }

    @Transactional
    public User updateById(Long id, UserRequest userRequest) {

        User user = getById(id);

        List<Role> roles = new ArrayList<>();
        userRequest.roles().stream().distinct().forEach(role ->
                roles.add(roleService.findByName(role.toUpperCase()))
        );

        user.setEmail(userRequest.email());
        user.setFirstName(userRequest.firstName());
        user.setLastName(userRequest.lastName());
        user.setRoles(roles);

        return userRepo.save(user);
    }

    @Transactional
    public Page<User> getUsers(Integer page, Integer size, List<String> sortParams, Map<String, String> filters) {
        try {
            Sort sort = buildSort(sortParams);
            Pageable pageable = PageRequest.of(page, size, sort);
            Specification<User> specification = buildSpecification(filters);
            return userRepo.findAll(specification, pageable);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new InvalidSearchQueryException("Invalid filters or sorting parameters");
        }
    }

    private Sort buildSort(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }
        sortParams.forEach(System.out::println);
        List<Sort.Order> orders = getOrders(sortParams);
        return Sort.by(orders);
    }

    private List<Sort.Order> getOrders(List<String> sortParams) {
        List<Sort.Order> orders = new ArrayList<>();
        for (String param : sortParams) {

            String[] parts = param.split("_");

            String property = parts[0];
            if (!Set.of("id", "firstname", "lastname", "email").contains(property.toLowerCase())) {
                throw new InvalidSearchQueryException("Invalid filters or sorting parameters");
            }
            Sort.Direction direction = Sort.Direction.ASC;

            if (parts.length > 1) {
                direction = Sort.Direction.fromString(parts[1]);
            }

            orders.add(new Sort.Order(direction, property));
        }
        return orders;
    }

    private Specification<User> buildSpecification(Map<String, String> filters) {
        Specification<User> spec = Specification.unrestricted();
        if (filters == null || filters.isEmpty()) {
            return spec;
        }

        if (filters.containsKey("firstName")) {
            String value = filters.get("firstName");
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("firstName"), value));
        }

        if (filters.containsKey("firstName_like")) {
            String value = filters.get("firstName_like");
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("firstName")), "%" + value.toLowerCase() + "%"));
        }

        if (filters.containsKey("lastName")) {
            String value = filters.get("lastName");
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("lastName"), value));
        }

        if (filters.containsKey("lastName_like")) {
            String value = filters.get("lastName_like");
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("lastName")), "%" + value.toLowerCase() + "%"));
        }

        return spec;
    }

}
