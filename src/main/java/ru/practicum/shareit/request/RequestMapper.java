package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;

@Service
public class RequestMapper {
    private final UserJpaRepository userJpaRepository;
    private final ItemJpaRepository itemJpaRepository;

    @Autowired
    public RequestMapper(UserJpaRepository userJpaRepository, ItemJpaRepository itemJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.itemJpaRepository = itemJpaRepository;
    }

    public RequestDto requestToDto(Request request) {
        return RequestDto.builder()
                .requestId(request.getRequestId())
                .requestDescription(request.getRequestDescription())
                .requestDate(request.getRequestDate())
                .itemsDtoForRequestList(RequestDto.ItemDtoForRequest.createItemsDtoForRequestList(itemJpaRepository
                        .findAllByRequestRequestId(request.getRequestId())))
                .build();
    }

    public Request dtoToRequest(RequestDto requestDto, Long userIdHeader) {
        return Request.builder()
                .requestId(requestDto.getRequestId())
                .requestDescription(requestDto.getRequestDescription())
                .requestor(userJpaRepository.findById(userIdHeader).orElse(null))
                .requestDate(LocalDateTime.now())
                .userIdHeader(userIdHeader)
                .build();
    }
}
