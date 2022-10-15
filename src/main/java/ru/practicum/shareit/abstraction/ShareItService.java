package ru.practicum.shareit.abstraction;

public interface ShareItService<V extends EntityDto> {
    /**
    Метод создаёт объект
    */
    V createEntityService(V entity, Long userIdHeader);

    /**
     Метод возвращает все объекты или один объект по заданному id
     */
    Object getEntityService(Long entityId, Long userIdHeader, String...additionalParams);

    /**
    Метод обновляет объект
    */
    V updateEntityService(Long entityId, V entity, Long userIdHeader);

    /**
    Метод удаляет все объекты или один объект по заданному id
    */
    void deleteEntityService(Long entityId);
}
