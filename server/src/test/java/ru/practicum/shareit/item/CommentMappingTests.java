package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentMappingTests {
    private final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final ItemJpaRepository mockItemJpaRepository = Mockito.mock(ItemJpaRepository.class);
    private final CommentMapper commentMapper = new CommentMapper(mockUserJpaRepository, mockItemJpaRepository);

    @Test
    void commentToDtoTests() {
        Comment comment1 = Comment.builder()
                .commentId(1L)
                .commentText("comment1 text")
                .commentItem(new Item())
                .author(new User())
                .commentDate(LocalDateTime.now())
                .userIdHeader(1L)
                .build();

        CommentDto commentDtoTest1 = commentMapper.commentToDto(comment1);

        assertEquals(comment1.getCommentId(), commentDtoTest1.getCommentId(), "Comment->CommentDto не совпадает id");
        assertEquals(comment1.getCommentText(), commentDtoTest1.getCommentText(), "Comment->CommentDto не совпадает text");
        assertNotNull(comment1.getCommentItem(), "Item null");
        assertNotNull(comment1.getAuthor(),"Author null");
        assertEquals(comment1.getCommentDate(), commentDtoTest1.getCommentDate(), "Comment->CommentDto не совпадает commentDate");
    }

    @Test
    void dtoToCommentTests() {
        CommentDto commentDto1 = CommentDto.builder()
                .commentId(1L)
                .commentText("commentDto1 text")
                .commentDate(LocalDateTime.now())
                .authorName("commentDto1Author")
                .itemId(1L)
                .userIdHeader(1L)
                .build();

        Comment commentTest1 = commentMapper.dtoToComment(commentDto1, 1L, 1L);

        commentTest1.setAuthor(User.builder().userName(commentDto1.getAuthorName()).build());
        commentTest1.setCommentItem(Item.builder().itemId(commentDto1.getItemId()).build());
        commentTest1.setUserIdHeader(commentDto1.getUserIdHeader());

        assertEquals(commentDto1.getCommentId(), commentTest1.getCommentId(), "CommentDto->Comment не совпадает id");
        assertEquals(commentDto1.getCommentText(), commentTest1.getCommentText(), "CommentDto->Comment не совпадает Text");
        assertEquals(commentDto1.getCommentDate().getMinute(), commentTest1.getCommentDate().getMinute(), "CommentDto->Comment не совпадает Date");
        assertEquals(commentDto1.getAuthorName(), commentTest1.getAuthor().getUserName(), "CommentDto->Comment имя автора не совпадает");
        assertEquals(commentDto1.getItemId(), commentTest1.getCommentItem().getItemId(), "CommentDto->Comment не совпадает itemId");
        assertEquals(commentDto1.getUserIdHeader(), commentTest1.getUserIdHeader(), "CommentDto->Comment не совпадает userIdHeader");

    }
}
