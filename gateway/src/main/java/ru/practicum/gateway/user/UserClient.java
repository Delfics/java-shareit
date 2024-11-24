package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.api.UserDto;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.utils.Utility;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post(Utility.EMPTY, userDto);
    }

    public ResponseEntity<Object> getAll() {
        return get(Utility.EMPTY);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get(Utility.SLASH + userId, userId);
    }

    public ResponseEntity<Object> update(Long userId, UserDto userDto) {
        return put(Utility.SLASH + userId, userId, userDto);
    }

    public ResponseEntity<Object> patch(Long userId, UserDto userDto) {
        return patch(Utility.SLASH + userId, userId, userDto);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete(Utility.SLASH + userId, userId);
    }
}
