package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.CommonService;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;

import java.util.Objects;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserService extends CommonService<User, UserDto> {

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage, UserMapper userMapper) {
        inMemoryUserStorage.setService(this);
        storage = inMemoryUserStorage;
        mapper = userMapper;
    }

    @Override
    public void validateEntityService(User user, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            user.setUserName(user.getEmail());
        }

        if (user.getEmail() == null || !Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE).matcher(user.getEmail()).find()) {
            excMsg += "Адрес электронной почты должен быть задан и иметь верный формат. ";
        }

        // Проверка на наличие дубликатов e-mail при создании и обновлении пользователя
        for (User thisUser : storage.getEntityMapStorage().values()) {
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
}
