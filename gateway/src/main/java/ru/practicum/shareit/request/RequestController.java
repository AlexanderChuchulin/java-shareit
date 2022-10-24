package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient itemClient;
    private final String entityName = "Request";

    @PostMapping
    public ResponseEntity<Object> createRequestController(@RequestHeader("X-Sharer-User-Id") long userIdHeader,
                                                       @RequestBody @Validated RequestDto requestDto) {
        log.info("Creating {} {}", entityName, requestDto);
        return itemClient.createRequestClient(userIdHeader, requestDto);
    }

    @GetMapping(value = {"", "/{requestId}", "/all"})
    public ResponseEntity<Object> getRequestsController(@PathVariable(required = false) Long requestId,
                                                     @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") String from,
                                                     @Positive @RequestParam(value = "size", required = false) String size,
                                                     @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {

        if (requestId == null) {
            if (ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri().toString().contains("all")) {
                requestId = -444555666L;
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("from", from);
            parameters.put("size", size);

            log.info("Get {}", entityName + "s");
            return itemClient.getRequestClient(requestId, userIdHeader, parameters);
        }
        log.info("Get {} by id={}", entityName, requestId);
        return itemClient.getRequestClient(requestId, userIdHeader, null);
    }

    @DeleteMapping(value = {"", "/{requestId}"})
    public ResponseEntity<Object> deleteUsersController(@PathVariable(required = false) Long requestId) {
        String path = "";

        if (requestId == null) {
            log.info("Delete {}", entityName + "s");
        } else {
            log.info("Delete {} by id={}", entityName, requestId);
            path = "/" + requestId;
        }
        return itemClient.deleteRequestClient(path);
    }
}


