package ru.practicum.explore_with_me.event.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CommentDto {
    private long id;
    private long userId;
    private String authorName;
    private long eventId;
    private String text;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp created;
}
