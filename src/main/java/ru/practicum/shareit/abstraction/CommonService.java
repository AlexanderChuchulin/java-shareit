package ru.practicum.shareit.abstraction;

import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class CommonService<T extends Entity, V extends EntityDto> implements Service<V> {
    protected Storage<T> storage;
    protected CommonMapper<T, V> mapper;

    @Override
    public V createEntity(V entityDto, Integer userIdHeader) {
        return mapper.entityToDto(storage.createEntity(mapper.dtoToEntity(entityDto, userIdHeader)));
    }

    @Override
    public V updateEntity(int entityId, V entityDto, Integer userIdHeader) {
        mapper.entityToDto(storage.updateEntity(entityId, mapper.dtoToEntity(entityDto, userIdHeader)));
        return mapper.entityToDto(storage.updateEntity(entityId, mapper.dtoToEntity(entityDto, userIdHeader)));
    }

    @Override
    public void deleteEntity(Integer entityId) {
        storage.deleteEntity(entityId);
    }

    @Override
    public Object getEntity(Integer entityId, Integer userIdHeader) {
        if (entityId == null) {
            ArrayList<T> entityList = (ArrayList<T>) storage.getEntity(null);

            if (userIdHeader != null) {
                return entityList.stream()
                        .filter(entity -> entity.getUserIdHeader().intValue() == userIdHeader.intValue())
                        .map(mapper::entityToDto)
                        .collect(Collectors.toList());
            } else {
                return entityList.stream()
                        .map(mapper::entityToDto)
                        .collect(Collectors.toList());
            }
        }
        return mapper.entityToDto((T) storage.getEntity(entityId));
    }
}
