package ru.practicum.shareit.abstraction;

public interface Service<V extends EntityDto> {
    // Метод создаёт объект
    V createEntity(V entity, Integer userIdHeader);

    // Метод обновляет объект
    V updateEntity(int entityId, V entity, Integer userIdHeader);

    // Метод удаляет все объекты или один объект по заданному id
    void deleteEntity(Integer entityId);

    // Метод возвращает все объекты или один объект по заданному id
    Object getEntity(Integer entityId, Integer userIdHeader);
}
