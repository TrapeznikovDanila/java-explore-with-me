package ru.practicum.exploreWithMe.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path, Map<String, Object> parameters) {
        return makeAndSendRequestWithoutBody(HttpMethod.GET, path, parameters);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequestWithBody(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequestWithoutBody(HttpMethod method, String path,
                                                          @Nullable Map<String, Object> parameters) {
        HttpEntity<T> requestEntity = new HttpEntity<>(null);

        ResponseEntity<Object> exploreWithMeServerResponse;
        try {
            if (parameters != null) {
                exploreWithMeServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                exploreWithMeServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(exploreWithMeServerResponse);
    }

    private <T> ResponseEntity<Object> makeAndSendRequestWithBody(HttpMethod method, String path,
                                                                     @Nullable Map<String, Object> parameters, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body);

        ResponseEntity<Object> exploreWithMeServerResponse;
        try {
            if (parameters != null) {
                exploreWithMeServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                exploreWithMeServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(exploreWithMeServerResponse);
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
