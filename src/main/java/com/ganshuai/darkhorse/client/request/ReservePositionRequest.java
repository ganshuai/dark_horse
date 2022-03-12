package com.ganshuai.darkhorse.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservePositionRequest {
    private String cardNo;
    private String flightNo;
}
