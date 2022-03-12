package com.ganshuai.darkhorse.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentOrder {
    private String paymentUrl;
    private String id;
    private Float amount;
    private String cardNo;
}
