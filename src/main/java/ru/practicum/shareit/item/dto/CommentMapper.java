package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {

  public static Comment toComment(CommentDto commentDto, long userId, long itemId) {
    return Comment.builder()
        .id(commentDto.getId())
        .text(commentDto.getText())
        .author(new User(userId))
        .item(new Item(itemId))
        .createDateTime(LocalDateTime.now())
        .build();
  }

  public static CommentDto toCommentDto(Comment comment) {
    return CommentDto.builder()
        .id(comment.getId())
        .text(comment.getText())
        .authorName(comment.getAuthor().getName())
        .created(comment.getCreateDateTime())
        .build();
  }
}
