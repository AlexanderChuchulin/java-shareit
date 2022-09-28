package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    @Query("select comment from Comment comment where comment.commentItem.itemId = ?1")
    List<Comment> findAllByItemId(Long itemId);
}
