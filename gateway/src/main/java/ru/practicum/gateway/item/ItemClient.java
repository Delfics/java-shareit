package ru.practicum.gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.api.CommentDto;
import ru.practicum.api.ItemDto;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.utils.Utility;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";
    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        return post(Utility.EMPTY, userId, itemDto);
    }

    public ResponseEntity<Object> createComment(CommentDto commentDto, Long itemId, Long userId) {
        return post(Utility.SLASH + itemId + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> getAll() {
        return get(Utility.EMPTY);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get(Utility.SLASH + userId, userId);
    }

    public ResponseEntity<Object> update(Long userId, ItemDto itemDto) {
        return put(Utility.SLASH + userId, userId, itemDto);
    }

    public ResponseEntity<Object> patchItem(Long itemId, ItemDto itemDto, Long userId) {
        return patch(Utility.SLASH + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete(Utility.SLASH + userId, userId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> parameter = Map.of("text", text);
        return searchItems("/search?text={text}", parameter);
    }

    public ResponseEntity<Object> getByIdItemWithComments(Long itemId, Long userId) {
        return get(Utility.SLASH + itemId, userId);
    }
}
