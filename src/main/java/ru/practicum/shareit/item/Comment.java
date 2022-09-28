package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.abstraction.ShareItEntity;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity @Table(name = "comments")
@Builder @Getter @Setter @AllArgsConstructor @RequiredArgsConstructor @ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment extends ShareItEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "comment_id")
    private Long commentId;
    @Column(name = "comment_text", nullable = false)
    private String commentText;
    @ManyToOne @JoinColumn(name="comment_item_id", referencedColumnName="item_id")
    private Item commentItem;
    @ManyToOne @JoinColumn(name="author_id", referencedColumnName="user_id")
    private User author;
    private LocalDateTime comment_date;
    @Transient
    private Long userIdHeader;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Comment comment = (Comment) o;
        return commentId != null && Objects.equals(commentId, comment.commentId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
