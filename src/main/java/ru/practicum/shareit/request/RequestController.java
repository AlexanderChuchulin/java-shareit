package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.abstraction.CommonController;

@RestController
@RequestMapping("/requests")
public class RequestController extends CommonController<RequestDto> {

    @Autowired
    public RequestController(RequestService requestService) {
        shareItService = requestService;
    }

    @GetMapping("/all")
    private Object getAllRequestsByAnyUserController(
            @RequestParam(value = "from", required = false, defaultValue = "0") String from,
            @RequestParam(value = "size", required = false) String size,
            @RequestHeader(value = "X-Sharer-User-Id") Long userIdHeader) {
        return shareItService.getEntityService(-444555666L, userIdHeader, from, size);
    }
}
