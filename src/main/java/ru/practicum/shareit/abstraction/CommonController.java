package ru.practicum.shareit.abstraction;

import org.springframework.web.bind.annotation.*;

public abstract class CommonController<V extends EntityDto> {
    protected ShareItService<V> shareItService;

    @PostMapping
    private V createEntityController(@RequestBody V entityDto,
                                     @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {
        return shareItService.createEntityService(entityDto, userIdHeader);
    }

    @PatchMapping(path = "/{entityId}")
    private V updateEntityController(@PathVariable Long entityId, @RequestBody V entityDto,
                                     @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {
        return shareItService.updateEntityService(entityId, entityDto, userIdHeader);
    }

    @DeleteMapping(value = {"", "/{entityId}"})
    private void deleteEntityController(@PathVariable(required = false) Long entityId) {
        shareItService.deleteEntityService(entityId);
    }

    @GetMapping(value = {"", "/{entityId}"})
    private Object getEntityController(@PathVariable(required = false) Long entityId,
                                       @RequestParam(value = "from", required = false, defaultValue = "0") String from,
                                       @RequestParam(value = "size", required = false) String size,
                                       @RequestParam(value = "state", required = false, defaultValue = "ALL") String bookingState,
                                       @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {
        return shareItService.getEntityService(entityId, userIdHeader, from, size, bookingState);
    }
}
