package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.ShareItService;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;
import ru.practicum.shareit.other.OtherUtils;
import ru.practicum.shareit.user.UserJpaRepository;

import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestService implements ShareItService<Request, RequestDto> {
    private final RequestJpaRepository requestJpaRepository;
    private final RequestMapper requestMapper;
    private final UserJpaRepository userJpaRepository;

    @Autowired
    public RequestService(RequestJpaRepository requestJpaRepository, RequestMapper requestMapper,
                          UserJpaRepository userJpaRepository) {
        this.requestJpaRepository = requestJpaRepository;
        this.requestMapper = requestMapper;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public RequestDto createEntityService(RequestDto requestDto, Long userIdHeader) {
        String conclusion = "Запрос не создан в БД.";

        validateEntityService(requestMapper.dtoToRequest(requestDto, userIdHeader), false, conclusion);
        log.info("Create Request DB " + requestMapper.dtoToRequest(requestDto, userIdHeader));
        return requestMapper.requestToDto(requestJpaRepository.save(requestMapper.dtoToRequest(requestDto, userIdHeader)));
    }

    @Override
    public Object getEntityService(Long requestId, Long userIdHeader, String... additionalParams) {
        if (!userJpaRepository.existsById(userIdHeader)) {
            throw new EntityNotFoundExc("Ошибка поиска Пользователя в БД. " +
                    "Get Requests by User ID Header " + userIdHeader + " прерван");
        }

        if (requestId == null) {
            log.info("Get All Requests by User ID Header");
            return requestJpaRepository.findAllByRequestorUserIdOrderByRequestDateDesc(userIdHeader).stream()
                    .map(requestMapper::requestToDto)
                    .collect(Collectors.toList());
        }

        if (requestId == -444555666L) {
            log.info("Get All Requests by Any User");
            return requestJpaRepository.findAllByRequestorUserIdNot(userIdHeader, OtherUtils.pageableCreateFrommAdditionalParams(
                    additionalParams, "Запросы", "requestDate")).stream()
                    .map(requestMapper::requestToDto)
                    .collect(Collectors.toList());
        }

        entityExistCheck(requestId, "Get Request by ID " + requestId);

        log.info("Get Request by Id " + requestId);
        return requestMapper.requestToDto(requestJpaRepository.getReferenceById(requestId));
    }

    @Override
    public RequestDto updateEntityService(Long requestId, RequestDto requestDto, Long userIdHeader) {
        return null;
    }

    @Override
    public void deleteEntityService(Long requestId) {
        if (requestId == null) {
            requestJpaRepository.deleteAll();
            log.info("Delete All Requests");
        } else {
            entityExistCheck(requestId, "Delete Request by id " + requestId);
            requestJpaRepository.deleteById(requestId);
            log.info("Delete Request by id " + requestId);
        }
    }

    @Override
    public void validateEntityService(Request request, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (request.getUserIdHeader() == null) {
            excMsg += "Ошибка авторизации - заголовок с id не задан. ";
        } else if (!userJpaRepository.existsById(request.getUserIdHeader())) {
            excMsg += "Пользователь с id " + request.getUserIdHeader() + " не найден. ";
        }

        if (request.getRequestDescription() == null || request.getRequestDescription().length() > 5000) {
            excMsg += "Описание Запроса должно быть задано и быть не более 5000 символов. ";
        }

        if (excMsg.length() > 0) {
            log.warn("Ошибка валидации Запроса. " + excMsg + conclusion);

            if (excMsg.contains("заголовок с id не задан")) {
                throw new MainPropDuplicateExc(excMsg + conclusion);
            } else if (excMsg.contains("Пользователь с id")) {
                throw new EntityNotFoundExc(excMsg + conclusion);
            } else {
                throw new ValidationExc(excMsg);
            }
        }
    }

    @Override
    public void entityExistCheck(Long requestId, String action) {
        if (!requestJpaRepository.existsById(requestId)) {
            throw new EntityNotFoundExc("Ошибка поиска Запроса в БД. " + action + " прервано.");
        }
    }
}
