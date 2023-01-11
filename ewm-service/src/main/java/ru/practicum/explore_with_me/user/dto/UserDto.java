package ru.practicum.explore_with_me.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    private Long id;
    @Email
    private String email;
    @NotNull
    private String name;
}
