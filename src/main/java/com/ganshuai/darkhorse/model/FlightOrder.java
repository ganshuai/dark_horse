package com.ganshuai.darkhorse.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightOrder {
    private Long id;
    private String cardNo;
    private String flightNo;
    private Float amount;
    private FlightOrderStatus status;
    private String paymentUrl;
    private String position;
    private String paymentId;
    private String reservePositionId;
}
