package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.abstraction.ShareItEntity;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request extends ShareItEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;
    @Column(name = "request_description", nullable = false)
    private String requestDescription;
    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "user_id")
    private User requestor;
    private LocalDateTime requestDate;
    @Transient
    private Long userIdHeader;
}
