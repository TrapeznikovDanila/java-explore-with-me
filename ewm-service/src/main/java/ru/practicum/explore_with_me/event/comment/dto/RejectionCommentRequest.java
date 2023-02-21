package ru.practicum.explore_with_me.event.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RejectionCommentRequest {
    @NotNull
    private Long id;
    @NotBlank
    private String rejectionReason;
}
