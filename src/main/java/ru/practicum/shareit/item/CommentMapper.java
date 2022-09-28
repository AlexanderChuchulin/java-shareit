package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;

@Service
public class CommentMapper {
    private final UserJpaRepository userJpaRepository;
    private final ItemJpaRepository itemJpaRepository;

    @Autowired
    public CommentMapper(UserJpaRepository userJpaRepository, ItemJpaRepository itemJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.itemJpaRepository = itemJpaRepository;
    }

    public CommentDto commentToDto(Comment comment) {
        return CommentDto.builder()
                .commentId(comment.getCommentId())
                .commentText(comment.getCommentText())
                .authorName(comment.getAuthor().getUserName())
                .comment_date(comment.getComment_date())
                .build();
    }

    public Comment dtoToComment(CommentDto commentDto, Long itemId, Long userIdHeader) {
        return Comment.builder()
                .commentId(commentDto.getCommentId())
                .commentText(commentDto.getCommentText())
                .commentItem(itemJpaRepository.findById(itemId).orElse(null))
                .author(userJpaRepository.findById(userIdHeader).orElse(null))
                .comment_date(LocalDateTime.now())
                .build();
    }
}
