package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.CommonService;
import ru.practicum.shareit.abstraction.Storage;
import ru.practicum.shareit.exception.AuthorizationExc;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ItemService extends CommonService<Item, ItemDto> {
    private final Storage<User> userStorage;

    @Autowired
    public ItemService(InMemoryItemStorage inMemoryItemStorage, InMemoryUserStorage inMemoryUserStorage) {
        inMemoryItemStorage.setService(this);
        storage = inMemoryItemStorage;
        userStorage = inMemoryUserStorage;
        mapper = new ItemMapper(userStorage);
    }

    List<ItemDto> getItemBySearchText(String searchText) {
        if (searchText.isBlank()) {
            return List.of();
        }
        return storage.getEntityMapStorage().values().stream()
                .filter(item -> item.getItemName().concat(item.getItemDesc()).toLowerCase().contains(searchText.toLowerCase()))
                .filter(Item::getIsItemAvailable)
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void validateEntityService(Item item, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (item.getUserIdHeader() == null) {
            excMsg += "Ошибка авторизации - заголовок с id не задан. ";
        } else if (!userStorage.getEntityMapStorage().containsKey(item.getUserIdHeader())) {
            excMsg += "Пользователь с id " + item.getUserIdHeader() + " не найден. ";
        } else if (isUpdate && item.getItemOwner().getId().intValue() != item.getUserIdHeader().intValue()) {
            excMsg += "Ошибка авторизации - попытка обновления вещи не владельцем. ";
        }

        if (item.getItemName() == null || item.getItemName().isBlank() || item.getItemName().length() > 50) {
            excMsg += "Название вещи должно быть задано и быть не более 50 символов. ";
        }

        if (item.getItemDesc() == null || item.getItemDesc().length() > 200) {
            excMsg += "Описание вещи должно быть задано и быть не более 200 символов. ";
        }

        if (item.getIsItemAvailable() == null) {
            excMsg += "Доступность вещи должна быть задана. ";
        }

        if (excMsg.length() > 0) {
            log.warn("Ошибка валидации вещи. " + excMsg + conclusion);

            if (excMsg.contains("заголовок с id не задан")) {
                throw new MainPropDuplicateExc(excMsg + conclusion);
            } else if (excMsg.contains("Пользователь с id")) {
                throw new EntityNotFoundExc(excMsg + conclusion);
            } else if (excMsg.contains("не владельцем")) {
                throw new AuthorizationExc(excMsg + conclusion);
            } else {
                throw new ValidationExc(excMsg);
            }
        }
    }
}
