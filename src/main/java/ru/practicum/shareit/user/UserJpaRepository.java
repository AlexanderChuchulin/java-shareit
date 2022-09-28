package ru.practicum.shareit.user;

import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.other.OtherUtils;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    User findByEmailContainingIgnoreCase(String emailSearch);

    default void updateUserById(Long userId, User updatingUser, UserService userService) {
        entityExistCheck(userId, "Update User by id " + userId);
        BeanUtils.copyProperties(getReferenceById(userId), updatingUser, OtherUtils.getNotNullPropertyNames(updatingUser));
        userService.validateEntityService(updatingUser, true, "Пользователь не обновлён в БД.");
    }

    default void entityExistCheck(Long userId, String action) {
        if (!existsById(userId)) {
            throw new EntityNotFoundExc("Ошибка поиска Пользователя в БД. " + action + " прервано.");
        }
    }
}
