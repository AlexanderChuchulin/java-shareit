package ru.practicum.shareit.abstraction;

import org.springframework.web.bind.annotation.*;

public abstract class CommonController<T extends Entity, V extends EntityDto> {
    protected CommonService<T, V> commonService;

    @PostMapping
    private V createEntityController(@RequestBody V entityDto,
                                     @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer userIdHeader) {
        return commonService.createEntity(entityDto, userIdHeader);
    }

    @PatchMapping("/{entityId}")
    private V updateEntityController(@PathVariable int entityId, @RequestBody V entityDto,
                                     @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer userIdHeader) {
        return commonService.updateEntity(entityId, entityDto, userIdHeader);
    }

    @DeleteMapping(value = {"", "/{entityId}"})
    private void deleteEntityController(@PathVariable(required = false) Integer entityId) {
        commonService.deleteEntity(entityId);
    }

    @GetMapping(value = {"", "/{entityId}"})
    private Object getEntityController(@PathVariable(required = false) Integer entityId,
                                       @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer userIdHeader) {
        return commonService.getEntity(entityId, userIdHeader);
    }
}
