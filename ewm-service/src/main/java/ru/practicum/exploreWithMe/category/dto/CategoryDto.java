package ru.practicum.exploreWithMe.category.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CategoryDto {
    @NotNull
    private long id;
    @NotNull
    private String name;
}
