package ru.practicum.shareit.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.abstraction.ShareItEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends ShareItEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "user_id")
    private Long userId;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Transient
    private Long userIdHeader;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return userId != null && Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
