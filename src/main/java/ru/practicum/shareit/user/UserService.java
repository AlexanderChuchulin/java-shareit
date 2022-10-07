package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.ShareItService;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;
import ru.practicum.shareit.other.OtherUtils;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements ShareItService<User, UserDto> {
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper, UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createEntityService(UserDto userDto, Long userIdHeader) {
        String conclusion = "Пользователь не создан в БД.";

        validateEntityService(userMapper.dtoToUser(userDto), false, conclusion);
        log.info("Create User DB " + userMapper.dtoToUser(userDto));
        return userMapper.userToDto(userJpaRepository.save(userMapper.dtoToUser(userDto)));
    }

    @Override
    public Object getEntityService(Long userId, Long userIdHeader, String... additionalParams) {
        if (userId == null) {
            log.info("Get All Users");
            return userJpaRepository.findAll().stream()
                    .map(userMapper::userToDto)
                    .collect(Collectors.toList());
        }
        entityExistCheck(userId, "Get User by ID " + userId);
        log.info("Get User By Id " + userId);
        return userMapper.userToDto(userJpaRepository.findById(userId).orElseThrow());
    }

    @Override
    public UserDto updateEntityService(Long userId, UserDto userDto, Long userIdHeader) {
        User updatingUser = userMapper.dtoToUser(userDto);

        entityExistCheck(userId, "Update User by id " + userId);

        BeanUtils.copyProperties(userJpaRepository
                .getReferenceById(userId), updatingUser, OtherUtils.getNotNullPropertyNames(updatingUser));

        validateEntityService(updatingUser, true, "Пользователь не обновлён в БД.");

        log.info("Update User by ID " + userId);
        return userMapper.userToDto(userJpaRepository.save(updatingUser));
    }

    @Override
    public void deleteEntityService(Long userId) {
        if (userId == null) {
            userJpaRepository.deleteAll();
            log.info("Delete All Users");
        } else {
            entityExistCheck(userId, "Delete User by ID " + userId);
            userJpaRepository.deleteById(userId);
            log.info("Delete User by ID " + userId);
        }
    }

    @Override
    public void validateEntityService(User user, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (user.getUserName() == null || user.getUserName().isBlank()) {
            user.setUserName(user.getEmail());
        }

        // Проверка на правильность e-mail и наличие дубликатов при создании и обновлении пользователя
        if (user.getEmail() == null || !Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE).matcher(user.getEmail()).find()) {
            excMsg += "Адрес электронной почты должен быть задан и иметь верный формат. ";
        } else if (userJpaRepository.findByEmailContainingIgnoreCase(user.getEmail()) != null) {
            if (isUpdate && userJpaRepository
                    .findByEmailContainingIgnoreCase(user.getEmail()).getUserId() != user.getUserId().longValue()) {
                excMsg += "Пользователь с e-mail " + user.getEmail() + " уже зарегистрирован. ";
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

    @Override
    public void entityExistCheck(Long userId, String action) {
        if (!userJpaRepository.existsById(userId)) {
            throw new EntityNotFoundExc("Ошибка поиска Пользователя в БД. " + action + " прервано.");
        }
    }
}
