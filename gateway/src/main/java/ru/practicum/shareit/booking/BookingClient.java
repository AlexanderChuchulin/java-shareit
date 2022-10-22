package ru.practicum.shareit.booking;

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
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBookingClient(long userIdHeader, BookingDto bookingDto) {
        return post("", userIdHeader, bookingDto);
    }

    @SneakyThrows
    public ResponseEntity<Object> getBookingClient(Long bookingId, Long userIdHeader, Map<String, Object> parameters) {
        if (bookingId != null && bookingId != -111222333L) {
            return get("/" + bookingId, userIdHeader);
        }

        if (userIdHeader == null) {
            throw new IllegalAccessException("Ошибка авторизации - заголовок с id не задан. " +
                    "Get Bookings by User ID Header " + userIdHeader + " прерван");
        }

        if (bookingId == null) {
            return get("?from={from}&size={size}&state={state}", userIdHeader, parameters);
        }
        return get("/owner?from={from}&size={size}&state={state}", userIdHeader, parameters);
    }

    public ResponseEntity<Object> updateBookingClient(String path, long userIdHeader, Map<String, Object> parameters) {
        return patch(path, userIdHeader, parameters);
    }

    public ResponseEntity<Object> deleteBookingClient(String path) {
        return delete(path);
    }
}