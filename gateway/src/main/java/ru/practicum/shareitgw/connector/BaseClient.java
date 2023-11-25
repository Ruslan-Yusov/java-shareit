package ru.practicum.shareitgw.connector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareitgw.exeption.BadRequestException;
import ru.practicum.shareitgw.exeption.ConflictException;
import ru.practicum.shareitgw.exeption.ResourceNotFoundException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BaseClient {
    public final RestTemplate restTemplate;

    @Value("${shareit-gateway.client.url}")
    private String baseUrl;

    public <R> ResponseEntity<R> get(String path, Class<R> responseClazz) {
        return get(path, null, null, responseClazz);
    }

    public <R> ResponseEntity<R> get(String path, Integer userId, Class<R> responseClazz) {
        return get(path, userId, null, responseClazz);
    }

    public <R> ResponseEntity<R> get(String path, Integer userId, @Nullable Map<String, Object> pathParameters, Class<R> responseClazz) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, pathParameters, null, null, responseClazz);
    }

    public <R> ResponseEntity<R> get(String path, Integer userId, @Nullable Map<String, Object> pathParameters, @Nullable Map<String, Object> queryParameters, Class<R> responseClazz) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, pathParameters, queryParameters, null, responseClazz);
    }

    public <T, R> ResponseEntity<R> post(String path, T body, Class<R> responseClazz) {
        return post(path, null, null, body, responseClazz);
    }

    public <T, R> ResponseEntity<R> post(String path, Integer userId, T body, Class<R> responseClazz) {
        return post(path, userId, null, body, responseClazz);
    }

    public <T, R> ResponseEntity<R> post(String path, Integer userId, @Nullable Map<String, Object> pathParameters, T body, Class<R> responseClazz) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, pathParameters, null, body, responseClazz);
    }

    public <T, R> ResponseEntity<R> put(String path, Integer userId, T body, Class<R> responseClazz) {
        return put(path, userId, null, body, responseClazz);
    }

    public <T, R> ResponseEntity<R> put(String path, Integer userId, @Nullable Map<String, Object> pathParameters, T body, Class<R> responseClazz) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, pathParameters, null, body, responseClazz);
    }

    public <T, R> ResponseEntity<R> patch(String path, T body, Class<R> responseClazz) {
        return patch(path, null, null, null, body, responseClazz);
    }

    public <R> ResponseEntity<R> patch(String path, Integer userId, Class<R> responseClazz) {
        return patch(path, userId, null, null, null, responseClazz);
    }

    public <T, R> ResponseEntity<R> patch(String path, Integer userId, T body, Class<R> responseClazz) {
        return patch(path, userId, null, null, body, responseClazz);
    }

    public <R> ResponseEntity<R> patch(String path, Integer userId, @Nullable Map<String, Object> queryParameters, Class<R> responseClazz) {
        return patch(path, userId, null, queryParameters, null, responseClazz);
    }

    public <T, R> ResponseEntity<R> patch(
            String path, Integer userId,
            @Nullable Map<String, Object> pathParameters,
            @Nullable Map<String, Object> queryParameters,
            T body, Class<R> responseClazz) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, pathParameters, queryParameters, body, responseClazz);
    }

    public ResponseEntity<Void> delete(String path) {
        return delete(path, null, null);
    }

    public ResponseEntity<Void> delete(String path, Integer userId) {
        return delete(path, userId, null);
    }

    public ResponseEntity<Void> delete(String path, Integer userId, @Nullable Map<String, Object> pathParameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, pathParameters, null, null, Void.class);
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(
            HttpMethod method, String path, Integer userId,
            @Nullable Map<String, Object> pathParameters,
            @Nullable Map<String, Object> queryParameters,
            @Nullable T body,
            Class<R> responseClazz
    ) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<R> shareitServerResponse;
        try {
            String url = baseUrl.concat(path);
            if (queryParameters != null && !queryParameters.isEmpty()) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
                queryParameters.forEach((key, value) -> builder.queryParam(key, List.of(value)));
                url = URLDecoder.decode(builder.toUriString(), "UTF-8");
            }
            if (pathParameters != null) {
                shareitServerResponse = restTemplate.exchange(url, method, requestEntity, responseClazz, pathParameters);
            } else {
                shareitServerResponse = restTemplate.exchange(url, method, requestEntity, responseClazz);
            }
            return shareitServerResponse;
        } catch (HttpStatusCodeException e) {
            handleHttpException(e);
            return null;
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private HttpHeaders defaultHeaders(Integer userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static void handleHttpException(HttpStatusCodeException e) {
        HttpStatus statusCode = e.getStatusCode();

        switch (statusCode) {
            case NOT_FOUND:
                throw new ResourceNotFoundException(e.getMessage());
            case BAD_REQUEST:
                throw new BadRequestException(e.getMessage());
            case CONFLICT:
                throw new ConflictException(e.getMessage());
            case SERVICE_UNAVAILABLE:
                throw new RuntimeException(e.getMessage());
        }
    }
}
