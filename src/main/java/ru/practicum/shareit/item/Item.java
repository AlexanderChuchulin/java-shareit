package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.abstraction.ShareItEntity;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity @Table(name = "items")
@Builder @Getter @Setter @AllArgsConstructor @RequiredArgsConstructor @ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item extends ShareItEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "item_id")
    private Long itemId;
    @Column(name = "item_name", nullable = false)
    private String itemName;
    @Column(name = "item_description", nullable = false)
    private String itemDescription;
    @Column(name = "available", nullable = false)
    private Boolean isItemAvailable;
    @ManyToOne @JoinColumn(name="owner_id", referencedColumnName="user_id")
    private User owner;
    @ManyToOne @JoinColumn(name="request_id", referencedColumnName="request_id")
    private ItemRequest itemRequest;
    @OneToMany @JoinColumn(name="booking_item_id")
    @ToString.Exclude
    private Set<Booking> bookingsSet;
    @OneToMany @JoinColumn(name="comment_item_id")
    @ToString.Exclude
    private Set<Comment> commentsSet;
    @Transient
    private Long userIdHeader;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Item item = (Item) o;
        return itemId != null && Objects.equals(itemId, item.itemId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
