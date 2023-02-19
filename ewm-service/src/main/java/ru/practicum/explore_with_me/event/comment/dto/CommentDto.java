package ru.practicum.explore_with_me.event.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    private Long userId;
    private String authorName;
    private Long eventId;
    private String text;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp created;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp updated;
    private String rejectionReason;
}
