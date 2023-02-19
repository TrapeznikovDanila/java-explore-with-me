package ru.practicum.explore_with_me.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private List<Long> events;
    private boolean pinned;
    @NotBlank
    private String title;
}
