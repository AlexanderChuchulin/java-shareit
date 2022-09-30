package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.abstraction.ShareItEntity;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "requests")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemRequest extends ShareItEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;
    @Column(name = "request_description", nullable = false)
    private String itemDescription;
    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "user_id")
    private User requestor;
    @Transient
    private Long userIdHeader;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ItemRequest that = (ItemRequest) o;
        return requestId != null && Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
