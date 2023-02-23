package ru.practicum.explore_with_me.category.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NewCategoryDto {
    @NotNull
    private String name;
}
