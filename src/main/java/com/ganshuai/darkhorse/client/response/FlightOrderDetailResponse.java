package com.ganshuai.darkhorse.client.response;

import com.ganshuai.darkhorse.model.FlightOrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightOrderDetailResponse {
    private Long id;
    private String cardNo;
    private String flightNo;
    private Float amount;
    private FlightOrderStatus status;
    private String position;
    private String paymentId;
    private String reservePositionId;
}
