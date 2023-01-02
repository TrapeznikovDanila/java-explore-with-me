package ru.practicum.exploreWithMe.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    private long id;
    @Email
    private String email;
    @NotNull
    private String name;
}
