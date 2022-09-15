package ru.practicum.shareit.abstraction;

import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class CommonService<T extends Entity, V extends EntityDto> implements Service<T, V> {
    protected Storage<T> storage;
    protected Mapper<T, V> mapper;

    @Override
    public V createEntityService(V entityDto, Long userIdHeader) {
        return mapper.entityToDto(storage.createEntityStorage(mapper.dtoToEntity(entityDto, userIdHeader)));
    }

    @Override
    public V updateEntityService(Long entityId, V entityDto, Long userIdHeader) {
        return mapper.entityToDto(storage.updateEntityStorage(entityId, mapper.dtoToEntity(entityDto, userIdHeader)));
    }

    @Override
    public void deleteEntityService(Long entityId) {
        storage.deleteEntityStorage(entityId);
    }

    @Override
    public Object getEntityService(Long entityId, Long userIdHeader) {
        if (entityId == null) {
            ArrayList<T> entityList = (ArrayList<T>) storage.getEntityStorage(null);

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
        return mapper.entityToDto((T) storage.getEntityStorage(entityId));
    }
}
