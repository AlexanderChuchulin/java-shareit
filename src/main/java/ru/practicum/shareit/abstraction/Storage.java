package ru.practicum.shareit.abstraction;

public interface Storage<T extends Entity> {
    // Метод создаёт объект
    T createEntity(T entity);

    // Метод обновляет объект
    T updateEntity(int entityId, T entity);

    // Метод удаляет все объекты или один объект по заданному id
    void deleteEntity(Integer entityId);

    // Метод возвращает все объекты или один объект по заданному id
    Object getEntity(Integer entityId);

    // Метод для валидации сущности
    void validateEntity(T entity, Boolean isUpdate, String conclusion);

    void entityExistCheck(int id, String action);
}
