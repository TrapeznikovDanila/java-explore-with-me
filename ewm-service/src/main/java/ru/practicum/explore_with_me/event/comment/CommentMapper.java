package ru.practicum.explore_with_me.event.comment;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.event.comment.dto.CommentDto;
import ru.practicum.explore_with_me.event.comment.dto.NewCommentDto;

@Component
public class CommentMapper {

    public static Comment makeComment(NewCommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }

    public static CommentDto makeCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setUserId(comment.getUser().getId());
        commentDto.setAuthorName(comment.getAuthorName());
        commentDto.setEventId(comment.getEvent().getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        if (comment.getUpdated() != null) {
            commentDto.setUpdated(comment.getUpdated());
        }
        if (comment.getRejectionReason() != null) {
            commentDto.setRejectionReason(comment.getRejectionReason());
        }
        return commentDto;
    }
}
