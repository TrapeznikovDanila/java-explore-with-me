package ru.practicum.explore_with_me.event.comment;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.event.comment.dto.CommentDto;
import ru.practicum.explore_with_me.event.comment.dto.NewCommentDto;
import ru.practicum.explore_with_me.event.comment.dto.UpdatedCommentDto;

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
        commentDto.setUserId(comment.getUserId());
        commentDto.setAuthorName(comment.getAuthorName());
        commentDto.setEventId(comment.getEventId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static UpdatedCommentDto makeUpdatedCommentDto(Comment comment) {
        UpdatedCommentDto commentDto = new UpdatedCommentDto();
        commentDto.setId(comment.getId());
        commentDto.setUserId(comment.getUserId());
        commentDto.setAuthorName(comment.getAuthorName());
        commentDto.setEventId(comment.getEventId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}
