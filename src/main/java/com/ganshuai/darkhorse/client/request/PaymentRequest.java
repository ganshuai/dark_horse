package com.ganshuai.darkhorse.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private String id;
    private Float amount;
    private String cardNo;
}
