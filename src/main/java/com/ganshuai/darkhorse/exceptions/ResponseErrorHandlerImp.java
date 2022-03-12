package com.ganshuai.darkhorse.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganshuai.darkhorse.model.Response;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

@RequiredArgsConstructor
public class ResponseErrorHandlerImp implements ResponseErrorHandler {
    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        int rawStatusCode = response.getRawStatusCode();
        HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
        return statusCode == null || statusCode.isError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        byte[] bytes = response.getBody().readAllBytes();
        Response applicationError = objectMapper.readValue(bytes, Response.class);
        throw new ApplicationError(response.getStatusCode(), applicationError.getCode(), applicationError.getMessage());
    }
}
