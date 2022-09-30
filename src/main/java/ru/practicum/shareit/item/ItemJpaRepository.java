package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerUserId(Long userIdHeader);

    @Query("select item from Item item where item.itemName like upper(concat('%', ?1, '%')) " +
            "or upper(item.itemDescription) like upper(concat('%', ?1, '%')) and item.isItemAvailable = true")
    List<Item> findAllAvailableItemsBySearchText(String searchText);
}
