package com.ganshuai.darkhorse.repository.entity;

import com.ganshuai.darkhorse.model.FlightOrderStatus;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flight_order")
public class FlightOrderEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String cardNo;
    private String flightNo;
    private Float amount;
    private FlightOrderStatus status;
    private String position;
    private String paymentId;
    private String reservePositionId;
}
