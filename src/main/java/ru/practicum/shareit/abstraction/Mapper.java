package ru.practicum.shareit.abstraction;

public interface Mapper<T extends Entity, V extends EntityDto> {
    /**
     Метод создаёт dto из entity
     */
    V entityToDto(T entity);

    /**
     Метод создаёт entity из dto
     */
    T dtoToEntity(V entityDto, Long userIdHeader);
}
