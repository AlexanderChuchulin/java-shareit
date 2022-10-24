package ru.practicum.shareit.request;

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
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequestClient(long userIdHeader, RequestDto requestDto) {
        return post("", userIdHeader, requestDto);
    }

    @SneakyThrows
    public ResponseEntity<Object> getRequestClient(Long requestId, Long userIdHeader, Map<String, Object> parameters) {
        if (requestId != null) {
            return get("/" + requestId, userIdHeader);
        }

        if (userIdHeader == null && parameters.get("text") != null) {
            throw new IllegalAccessException("Ошибка авторизации - заголовок с id не задан. " +
                    "Get Requests by User ID Header " + userIdHeader + " прерван");
        }

        if (parameters.get("text") == null) {
            return get("?from={from}&size={size}", userIdHeader, parameters);
        }
        return get("/search?text={text}&from={from}&size={size}", userIdHeader, parameters);
    }

    public ResponseEntity<Object> updateRequestClient(String path, Long userIdHeader, RequestDto requestDto) {
        return patch(path, userIdHeader, requestDto);
    }

    public ResponseEntity<Object> deleteRequestClient(String path) {
        return delete(path);
    }
}