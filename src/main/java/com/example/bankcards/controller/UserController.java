package com.example.bankcards.controller;

import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.UserService;
import com.example.bankcards.util.ModelDTOConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public PageResponse<UserResponse> getUsers(@RequestParam(defaultValue = "0") @Valid Integer page,
                                 @RequestParam(defaultValue = "10") @Valid Integer size,
                                 @RequestParam(required = false) @Valid List<String> sort,
                                 @RequestParam(required = false) @Valid Map<String, String> filters) {
        Page<User> usersPage = userService.getUsers(page, size, sort, filters);
        List<UserResponse> items = ModelDTOConverter.convert(usersPage.getContent());
        return new PageResponse<>(
                items,
                page,
                size,
                usersPage.getTotalElements(),
                usersPage.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable @Valid Long id) {
        User user = userService.getById(id);
        return ModelDTOConverter.convert(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable @Valid Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable @Valid Long id, @Valid @RequestBody UserRequest userRequest) {
        User user = userService.updateById(id, userRequest);
        return ModelDTOConverter.convert(user);
    }

}
