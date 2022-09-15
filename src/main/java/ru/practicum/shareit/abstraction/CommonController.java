package ru.practicum.shareit.abstraction;

import org.springframework.web.bind.annotation.*;

public abstract class CommonController<T extends Entity, V extends EntityDto> {
    protected Service<T, V> service;

    @PostMapping
    private V createEntityController(@RequestBody V entityDto,
                                     @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {
        return service.createEntityService(entityDto, userIdHeader);
    }

    @PatchMapping("/{entityId}")
    private V updateEntityController(@PathVariable Long entityId, @RequestBody V entityDto,
                                     @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {
        return service.updateEntityService(entityId, entityDto, userIdHeader);
    }

    @DeleteMapping(value = {"", "/{entityId}"})
    private void deleteEntityController(@PathVariable(required = false) Long entityId) {
        service.deleteEntityService(entityId);
    }

    @GetMapping(value = {"", "/{entityId}"})
    private Object getEntityController(@PathVariable(required = false) Long entityId,
                                       @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {
        return service.getEntityService(entityId, userIdHeader);
    }
}
