package ru.practicum.shareit.abstraction;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.other.OtherUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public abstract class InMemoryStorage<T extends Entity, V extends EntityDto> implements Storage<T> {
    private long startId;
    private Map<Long, T> entityMap = new HashMap<>();
    private Service<T, V> service;

    public T createEntityStorage(T entity) {
        String conclusion = entity.getClass().getSimpleName() + " не создан в памяти.";

        service.validateEntityService(entity, false, conclusion);
        entity.setId(generateId());
        entityMap.put(entity.getId(), entity);

        log.info("createEntity " + entity);
        return entity;
    }

    public T updateEntityStorage(Long entityId, T entity) {
        String conclusion = entity.getClass().getSimpleName() + " не обновлён в памяти.";
        entityExistCheck(entityId, "updateEntity");

        BeanUtils.copyProperties(entityMap.get(entityId), entity, OtherUtils.getNotNullPropertyNames(entity));

        service.validateEntityService(entity, true, conclusion);
        entityMap.put(entityId, entity);
        log.info("updateEntity " + entity);
        return entityMap.get(entityId);
    }

    public void deleteEntityStorage(Long entityId) {
        if (entityId == null) {
            entityMap.clear();
            log.info("deleteEntity (all)");
        } else {
            entityExistCheck(entityId, "deleteEntity (by id)");
            entityMap.remove(entityId);
            log.info("deleteEntity (by id) " + entityId);
        }
    }

    public Object getEntityStorage(Long entityId) {
        if (entityId == null) {
            log.info("getAllEntity by header Id");
            return new ArrayList<>(entityMap.values());
        } else {
            entityExistCheck(entityId, "getOneEntity (by id)");
            log.info("getEntityById " + entityMap.get(entityId));
            return entityMap.get(entityId);
        }
    }

    @Override
    public Map<Long, T> getEntityMapStorage() {
        return entityMap;
    }

    public void entityExistCheck(Long id, String action) {
        String excMsg = "";

        if (!entityMap.containsKey(id)) {
            excMsg += "Ошибка поиска объектов. " + action + " прервано.";
            log.warn(excMsg);
            throw new EntityNotFoundExc(excMsg);
        }
    }

    public long generateId() {
        long id = startId;

        if (!entityMap.isEmpty()) {
            for (Long currentId : entityMap.keySet()) {
                if (currentId > id) {
                    id = currentId;
                }
            }
        }

        id++;
        setStartId(id);
        return id;
    }
}
