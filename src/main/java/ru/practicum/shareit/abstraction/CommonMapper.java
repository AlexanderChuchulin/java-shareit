package ru.practicum.shareit.abstraction;

public abstract class CommonMapper<T extends Entity, V extends EntityDto> {

    // Метод создаёт dto из entity
    public abstract V entityToDto(T entity);

    // Метод создаёт entity из dto
    protected abstract T dtoToEntity(V entityDto, Integer userIdHeader);
}
