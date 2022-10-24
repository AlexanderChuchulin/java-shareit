package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.ShareItService;
import ru.practicum.shareit.booking.BookingJpaRepository;
import ru.practicum.shareit.exception.AuthorizationExc;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;
import ru.practicum.shareit.other.OtherUtils;
import ru.practicum.shareit.user.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ItemService implements ShareItService<ItemDto> {
    private final ItemJpaRepository itemJpaRepository;
    private final ItemMapper itemMapper;
    private final CommentJpaRepository commentJpaRepository;
    private final CommentMapper commentMapper;
    private final UserJpaRepository userJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;

    @Autowired
    public ItemService(ItemJpaRepository itemJpaRepository, ItemMapper itemMapper,
                       CommentJpaRepository commentJpaRepository, CommentMapper commentMapper,
                       UserJpaRepository userJpaRepository, BookingJpaRepository bookingJpaRepository) {
        this.itemJpaRepository = itemJpaRepository;
        this.itemMapper = itemMapper;
        this.commentJpaRepository = commentJpaRepository;
        this.commentMapper = commentMapper;
        this.userJpaRepository = userJpaRepository;
        this.bookingJpaRepository = bookingJpaRepository;
    }

    @Override
    public ItemDto createEntityService(ItemDto itemDto, Long userIdHeader) {
        String conclusion = "Вещь не создана в БД.";

        validateItemService(itemMapper.dtoToItem(itemDto, userIdHeader), false, conclusion);
        log.info("Create Item DB");
        return itemMapper.itemToDto(itemJpaRepository.save(itemMapper.dtoToItem(itemDto, userIdHeader)));
    }

    public CommentDto createCommentService(CommentDto commentDto, Long itemId, Long userIdHeader) {
        if (bookingJpaRepository.findAllByBookerIdAndItemIdAndTime(itemId, userIdHeader, LocalDateTime.now()).isEmpty()
                || commentDto.getCommentText().isBlank()) {
            throw new ValidationExc("Комментарий не соответствует условиям Бронирования или текст пустой");
        }
        return commentMapper.commentToDto(commentJpaRepository.save(commentMapper.dtoToComment(commentDto, itemId, userIdHeader)));
    }

    @Override
    public Object getEntityService(Long itemId, Long userIdHeader, String... additionalParams) {
        if (!userJpaRepository.existsById(userIdHeader)) {
            throw new EntityNotFoundExc("Ошибка поиска Пользователя в БД. " +
                    "Get Item by User ID Header " + itemId + " прерван");
        }

        if (itemId == null) {
            log.info("Get All Items by User ID Header");
            return itemJpaRepository.findAllByOwnerUserIdOrderByItemIdAsc(userIdHeader,
                    OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, 10)).stream()
                    .map(item -> itemMapper.itemToDto(item, true))
                    .collect(Collectors.toList());
        }

        itemExistCheck(itemId, "Get Item by ID " + itemId);

        if (itemJpaRepository.getReferenceById(itemId).getOwner().getUserId() == userIdHeader.longValue()) {
            log.info("Get Item by Id " + itemId + " For Owner with Id " + userIdHeader);
            return itemMapper.itemToDto(itemJpaRepository.getReferenceById(itemId), true);
        } else {
            log.info("Get Item by Id " + itemId + " For Not Owner with Id " + userIdHeader);
        }
        return itemMapper.itemToDto(itemJpaRepository.getReferenceById(itemId), false);
    }

    public List<ItemDto> getItemBySearchText(String searchText, String... additionalParams) {
        if (searchText.isBlank()) {
            return List.of();
        }
        return itemJpaRepository.findAllAvailableItemsBySearchText(searchText,
                OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, 10)).stream()
                .map(itemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateEntityService(Long itemId, ItemDto itemDto, Long userIdHeader) {
        Item updatingItem = itemMapper.dtoToItem(itemDto, userIdHeader);

        itemExistCheck(itemId, "Update Item by id " + itemId);

        BeanUtils.copyProperties(itemJpaRepository
                .getReferenceById(itemId), updatingItem, OtherUtils.getNotNullPropertyNames(updatingItem));

        validateItemService(updatingItem, true, "Вещь не обновлена в БД.");

        updatingItem.setUserIdHeader(updatingItem.getOwner().getUserId());

        log.info("Update Item by ID " + itemId);
        return itemMapper.itemToDto(itemJpaRepository.save(updatingItem));
    }

    @Override
    public void deleteEntityService(Long itemId) {
        if (itemId == null) {
            itemJpaRepository.deleteAll();
            log.info("Delete All Items");
        } else {
            itemExistCheck(itemId, "Delete Item by id " + itemId);
            itemJpaRepository.deleteById(itemId);
            log.info("Delete Item by id " + itemId);
        }
    }

    public void validateItemService(Item item, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (item.getUserIdHeader() == null) {
            excMsg += "Ошибка авторизации - заголовок с id не задан. ";
        } else if (!userJpaRepository.existsById(item.getUserIdHeader())) {
            excMsg += "Пользователь с id " + item.getUserIdHeader() + " не найден. ";
        } else if (isUpdate && itemJpaRepository.getReferenceById(item.getItemId()).getOwner().getUserId()
                != item.getUserIdHeader().longValue()) {
            excMsg += "Ошибка авторизации - попытка обновления вещи не владельцем. ";
        }

        if (item.getItemName() == null || item.getItemName().isBlank() || item.getItemName().length() > 255) {
            excMsg += "Название вещи должно быть задано и быть не более 255 символов. ";
        }

        if (item.getItemDescription() == null || item.getItemDescription().isBlank() || item.getItemDescription().length() > 5000) {
            excMsg += "Описание вещи должно быть задано и быть не более 5000 символов. ";
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

    public void itemExistCheck(Long itemId, String action) {
        if (!itemJpaRepository.existsById(itemId)) {
            throw new EntityNotFoundExc("Ошибка поиска Вещи в БД. " + action + " прервано.");
        }
    }
}
