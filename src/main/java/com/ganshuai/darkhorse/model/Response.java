package com.ganshuai.darkhorse.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Response {
    private final String code;
    private final String message;
    private final Object data;
}
