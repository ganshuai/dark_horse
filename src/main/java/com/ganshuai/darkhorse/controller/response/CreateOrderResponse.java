package com.ganshuai.darkhorse.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderResponse {
    private String paymentUrl;
    private Long id;
    private Float amount;
    private String cardNo;
}
