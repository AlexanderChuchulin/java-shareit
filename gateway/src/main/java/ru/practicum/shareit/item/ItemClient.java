package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItemClient(long userIdHeader, ItemDto itemDto) {
        return post("", userIdHeader, itemDto);
    }

    public ResponseEntity<Object> createCommentClient(long itemId, long userIdHeader, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userIdHeader, commentDto);
    }

    @SneakyThrows
    public ResponseEntity<Object> getItemClient(Long itemId, Long userIdHeader, Map<String, Object> parameters) {
        if (itemId != null) {
            return get("/" + itemId, userIdHeader);
        }

        if (userIdHeader == null && parameters.get("text") != null) {
            throw new IllegalAccessException("Ошибка авторизации - заголовок с id не задан. " +
                    "Get Items by User ID Header " + userIdHeader + " прерван");
        }

        if (parameters.get("text") == null) {
            return get("?from={from}&size={size}", userIdHeader, parameters);
        }
        return get("/search?text={text}&from={from}&size={size}", userIdHeader, parameters);
    }

    public ResponseEntity<Object> updateItemClient(String path, Long userIdHeader, ItemDto itemDto) {
        return patch(path, userIdHeader, itemDto);
    }

    public ResponseEntity<Object> deleteItemClient(String path) {
        return delete(path);
    }
}