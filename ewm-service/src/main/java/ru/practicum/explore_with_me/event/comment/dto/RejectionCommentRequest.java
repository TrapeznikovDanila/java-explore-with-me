package ru.practicum.explore_with_me.event.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RejectionCommentRequest {
    @NotNull
    private long id;
    @NotBlank
    private String rejectionReason;
}
