package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.abstraction.ShareItEntity;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment extends ShareItEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;
    @Column(name = "comment_text", nullable = false)
    private String commentText;
    @ManyToOne
    @JoinColumn(name = "comment_item_id", referencedColumnName = "item_id")
    private Item commentItem;
    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "user_id")
    private User author;
    private LocalDateTime commentDate;
    @Transient
    private Long userIdHeader;
}
