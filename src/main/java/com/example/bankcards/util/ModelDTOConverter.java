package com.example.bankcards.util;

import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;

import java.util.List;

public class ModelDTOConverter {

    public static UserResponse convert(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream().map(Role::getName).toList()
        );
    }

    public static List<UserResponse> convert(List<User> users) {
        return users.stream().map(ModelDTOConverter::convert).toList();
    }

}
