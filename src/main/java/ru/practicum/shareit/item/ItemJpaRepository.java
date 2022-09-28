package ru.practicum.shareit.item;

import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.other.OtherUtils;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {

    default void updateItemById(Long itemId, Item updatingItem, ItemService itemService) {
        entityExistCheck(itemId, "Update User by id " + itemId);
        BeanUtils.copyProperties(getReferenceById(itemId), updatingItem, OtherUtils.getNotNullPropertyNames(updatingItem));
        itemService.validateEntityService(updatingItem, true, "Вещь не обновлена в БД.");
        updatingItem.setUserIdHeader(updatingItem.getOwner().getUserId());
    }

    @Query("select item from Item item where item.owner.userId = ?1")
    List<Item> findAllByOwnerId(Long userIdHeader);

    @Query("select item from Item item where item.itemName like upper(concat('%', ?1, '%')) " +
            "or upper(item.itemDescription) like upper(concat('%', ?1, '%')) and item.isItemAvailable = true")
    List<Item> getAvailableItemsBySearchText(String searchText);

    default void entityExistCheck(Long itemId, String action) {
        if (!existsById(itemId)) {
            throw new EntityNotFoundExc("Ошибка поиска Вещи в БД. " + action + " прервано.");
        }
    }
}
