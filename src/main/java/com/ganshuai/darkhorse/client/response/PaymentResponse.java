package com.ganshuai.darkhorse.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {
    private String paymentUrl;
    private String id;
    private Float amount;
    private String cardNo;
}
