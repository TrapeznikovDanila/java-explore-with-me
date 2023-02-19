package ru.practicum.explore_with_me.category.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    @NotNull
    private Long id;
    @NotNull
    private String name;
}
