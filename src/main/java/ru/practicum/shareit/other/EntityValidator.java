package ru.practicum.shareit.other;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AuthorizationExc;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.User;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@Slf4j
public class EntityValidator {
    private static Map<Integer, User> inMemoryUserMap;

    @Autowired
    public EntityValidator(InMemoryUserStorage inMemoryUserStorage) {
        EntityValidator.inMemoryUserMap = inMemoryUserStorage.getEntityMap();
    }

    public static void validateUser(User user, String conclusion) {
        String excMsg = "";

        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            user.setUserName(user.getEmail());
        }

        if (user.getEmail() == null || !Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE).matcher(user.getEmail()).find()) {
            excMsg += "Адрес электронной почты должен быть задан и иметь верный формат. ";
        }

        // Проверка на наличие дубликатов e-mail при создании и обновлении пользователя
        for (User thisUser : inMemoryUserMap.values()) {
            if (user.getEmail() != null && !Objects.equals(user.getId(), thisUser.getId())
                    && user.getEmail().equalsIgnoreCase(thisUser.getEmail())) {
                excMsg += "Пользователь с e-mail " + user.getEmail() + " уже зарегистрирован. ";
                break;
            }
        }

        if (excMsg.length() > 0) {
            log.warn("Ошибка валидации пользователя. " + excMsg + conclusion);

            if (excMsg.contains("Пользователь с e-mail")) {
                throw new MainPropDuplicateExc(excMsg + conclusion);
            } else {
                throw new ValidationExc(excMsg + conclusion);
            }
        }
    }

    public static void validateItem(Item item, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (item.getUserIdHeader() == null) {
            excMsg += "Ошибка авторизации - заголовок с id не задан. ";
        } else if (!inMemoryUserMap.containsKey(item.getUserIdHeader())) {
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
