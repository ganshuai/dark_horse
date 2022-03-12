package com.ganshuai.darkhorse.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservePosition {
    private String id;
    private String cardNo;
    private String flightNo;
    private String position;
    private Float amount;
}
