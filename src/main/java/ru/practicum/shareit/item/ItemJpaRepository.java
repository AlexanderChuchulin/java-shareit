package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerUserId(Long userIdHeader, Pageable pageable);

    @Query("select item from Item item where item.itemName like upper(concat('%', ?1, '%')) " +
            "or upper(item.itemDescription) like upper(concat('%', ?1, '%')) and item.isItemAvailable = true")
    Page<Item> findAllAvailableItemsBySearchText(String searchText, Pageable pageable);

    List<Item> findAllByRequestRequestId(Long requestId);
}
