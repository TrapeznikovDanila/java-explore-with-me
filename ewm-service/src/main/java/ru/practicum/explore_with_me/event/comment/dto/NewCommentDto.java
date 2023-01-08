package ru.practicum.explore_with_me.event.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCommentDto {
    @NotBlank
    @Size(max = 500, min = 2)
    private String text;
}
