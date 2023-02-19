package ru.practicum.explore_with_me.event.comment;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.event.comment.dto.CommentDto;
import ru.practicum.explore_with_me.event.comment.dto.NewCommentDto;

import java.util.Optional;

@Component
public class CommentMapper {

    public static Comment makeComment(NewCommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText()).build();
    }

    public static CommentDto makeCommentDto(Comment comment) {
        CommentDto commentDto = CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .authorName(comment.getAuthorName())
                .eventId(comment.getEvent().getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
        Optional.ofNullable(comment.getUpdated()).ifPresent(commentDto::setUpdated);
        Optional.ofNullable(comment.getRejectionReason()).ifPresent(commentDto::setRejectionReason);
        return commentDto;
    }
}
