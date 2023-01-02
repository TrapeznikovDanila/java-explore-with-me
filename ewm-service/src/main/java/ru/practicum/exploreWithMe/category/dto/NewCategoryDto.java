package ru.practicum.exploreWithMe.category.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NewCategoryDto {
    @NotNull
    private String name;
}
