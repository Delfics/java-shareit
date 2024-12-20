package ru.practicum.gateway.user.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.api.dto.UserDto;
import ru.practicum.gateway.user.UserClient;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserClientTest {

    private UserClient userClient;

    @MockBean
    private RestTemplate restTemplate ;

    @Mock
    private ResponseEntity<Object> responseEntity;

    private static final String USER_URL = "/users";
    private static final String SERVER_URL = "http://localhost:8080";


    @BeforeEach
    void setUp() {
        // Настройка RestTemplateBuilder вручную
        RestTemplateBuilder restTemplateBuilder = Mockito.mock(RestTemplateBuilder.class);

        // Мокируем uriTemplateHandler для RestTemplateBuilder
        /*DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(SERVER_URL + USER_URL);*/
        Mockito.when(restTemplateBuilder.uriTemplateHandler(Mockito.any(DefaultUriBuilderFactory.class)))
                .thenReturn(restTemplateBuilder);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        // Мокируем requestFactory, чтобы он возвращал тот же RestTemplateBuilder
        /*restTemplateBuilder.requestFactory(() -> factory);*/

        Mockito.when(restTemplateBuilder.requestFactory((Class<? extends ClientHttpRequestFactory>) Mockito.any()))
                .thenReturn(restTemplateBuilder);

        // Мокируем метод build(), чтобы он возвращал наш RestTemplate
        Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);

        // Создаем экземпляр UserClient вручную, передавая мок RestTemplateBuilder
        userClient = new UserClient(SERVER_URL, restTemplateBuilder);
    }

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        when(restTemplate.exchange(eq(SERVER_URL + USER_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        ResponseEntity<Object> response = userClient.create(userDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testGetAllUsers() {
        when(restTemplate.exchange(eq(SERVER_URL + USER_URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<Object> response = userClient.getAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testGetUserById() {
        Long userId = 1L;
        when(restTemplate.exchange(eq(SERVER_URL + USER_URL + "/1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<Object> response = userClient.getById(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        when(restTemplate.exchange(eq(SERVER_URL + USER_URL + "/1"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<Object> response = userClient.delete(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
