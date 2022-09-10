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
public abstract class InMemoryStorage<T extends Entity> implements Storage<T> {
    private int startId;
    private Map<Integer, T> entityMap = new HashMap<>();

    public T createEntity(T entity) {
        String conclusion = entity.getClass().getSimpleName() + " не создан в памяти.";

        validateEntity(entity, false, conclusion);
        entity.setId(generateId());
        entityMap.put(entity.getId(), entity);

        log.info("createEntity " + entity);
        return entity;
    }

    public T updateEntity(int entityId, T entity) {
        String conclusion = entity.getClass().getSimpleName() + " не обновлён в памяти.";
        entityExistCheck(entityId, "updateEntity");

        BeanUtils.copyProperties(entityMap.get(entityId), entity, OtherUtils.getNotNullPropertyNames(entity));

        validateEntity(entity, true, conclusion);
        entityMap.put(entityId, entity);
        log.info("updateEntity " + entity);
        return entityMap.get(entityId);
    }

    public void deleteEntity(Integer entityId) {
        if (entityId == null) {
            entityMap.clear();
            log.info("deleteEntity (all)");
        } else {
            entityExistCheck(entityId, "deleteEntity (by id)");
            entityMap.remove(entityId);
            log.info("deleteEntity (by id) " + entityId);
        }
    }

    public Object getEntity(Integer entityId) {
        if (entityId == null) {
            log.info("getAllEntity by header Id");
            return new ArrayList<>(entityMap.values());
        } else {
            entityExistCheck(entityId, "getOneEntity (by id)");
            log.info("getEntityById " + entityMap.get(entityId));
            return entityMap.get(entityId);
        }
    }

    public void entityExistCheck(int id, String action) {
        String excMsg = "";

        if (!entityMap.containsKey(id)) {
            excMsg += "Ошибка поиска объектов. " + action + " прервано.";
            log.warn(excMsg);
            throw new EntityNotFoundExc(excMsg);
        }
    }

    public int generateId() {
        int id = startId;

        if (!entityMap.isEmpty()) {
            for (Integer currentId : entityMap.keySet()) {
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
