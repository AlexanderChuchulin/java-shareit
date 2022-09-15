package ru.practicum.shareit.abstraction;

import java.util.Map;

public interface Storage<T extends Entity> {
    /**
    Метод создаёт объект
    */
    T createEntityStorage(T entity);

    /**
    Метод обновляет объект
    */
    T updateEntityStorage(Long entityId, T entity);

    /**
    Метод удаляет все объекты или один объект по заданному id
    */
    void deleteEntityStorage(Long entityId);

    /**
    Метод возвращает все объекты или один объект по заданному id
    */
    Object getEntityStorage(Long entityId);

    /**
     Метод возвращает таблицу всех объектов
     */
    Map<Long, T> getEntityMapStorage();

    /**
    Метод проверяет существование объекта
    */
    void entityExistCheck(Long entityId, String action);
}
