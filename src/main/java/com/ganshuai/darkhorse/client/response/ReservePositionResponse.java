package com.ganshuai.darkhorse.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservePositionResponse {
    private String id;
    private String cardNo;
    private String flightNo;
    private String position;
    private Float amount;
}
