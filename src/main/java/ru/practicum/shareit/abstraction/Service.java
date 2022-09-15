package ru.practicum.shareit.abstraction;

public interface Service<T extends Entity, V extends EntityDto> {
    /**
    Метод создаёт объект
    */
    V createEntityService(V entity, Long userIdHeader);

    /**
    Метод обновляет объект
    */
    V updateEntityService(Long entityId, V entity, Long userIdHeader);

    /**
    Метод удаляет все объекты или один объект по заданному id
    */
    void deleteEntityService(Long entityId);

    /**
    Метод возвращает все объекты или один объект по заданному id
    */
    Object getEntityService(Long entityId, Long userIdHeader);

    /**
     Метод для валидации сущности
     */
    void validateEntityService(T entity, Boolean isUpdate, String conclusion);
}
