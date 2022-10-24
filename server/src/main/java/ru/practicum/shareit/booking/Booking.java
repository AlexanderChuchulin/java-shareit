package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.abstraction.ShareItEntity;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Booking extends ShareItEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;
    private LocalDateTime bookingStart;
    private LocalDateTime bookingEnd;
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
    @ManyToOne
    @JoinColumn(name = "booking_item_id", referencedColumnName = "item_id")
    private Item bookingItem;
    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "user_id")
    private User booker;
    @Transient
    private Long userIdHeader;
}
