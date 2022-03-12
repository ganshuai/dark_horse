package com.ganshuai.darkhorse.exceptions;

import com.ganshuai.darkhorse.model.Response;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ResponseAdvice extends ResponseEntityExceptionHandler implements ResponseBodyAdvice<Object> {
    @ExceptionHandler(ApplicationError.class)
    public ResponseEntity<Response> handleApplicationException(ApplicationError exception) {
        return new ResponseEntity<>(new Response(exception.getCode(), exception.getMessage(), null), exception.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleApplicationException(Exception exception) {
        return new ResponseEntity<>(new Response("50001", "服务错误", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public boolean supports(final MethodParameter returnType, final Class<? extends HttpMessageConverter<?>> converterType) {
        return false;
    }

    @Override
    public Response beforeBodyWrite(final Object body, final MethodParameter returnType,
                                            final MediaType selectedContentType,
                                            final Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                            final ServerHttpRequest request,
                                            final ServerHttpResponse response) {

        if (body instanceof ApplicationError) {
            ApplicationError error = (ApplicationError) body;
            return new Response(error.getCode(), error.getMessage(), null);
        }

        return new Response("200", "success", body);
    }
}
