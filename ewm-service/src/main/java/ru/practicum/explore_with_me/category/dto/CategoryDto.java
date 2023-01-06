package ru.practicum.explore_with_me.category.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CategoryDto {
    @NotNull
    private long id;
    @NotNull
    private String name;
}
