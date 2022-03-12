package com.ganshuai.darkhorse.controller;

import com.ganshuai.darkhorse.client.response.FlightOrderDetailResponse;
import com.ganshuai.darkhorse.controller.request.CreateOrderRequest;
import com.ganshuai.darkhorse.controller.response.CreateOrderResponse;
import com.ganshuai.darkhorse.model.FlightOrder;
import com.ganshuai.darkhorse.service.BookFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookFlightController {
    private final BookFlightService bookFlightService;

    @PostMapping("/orders")
    public CreateOrderResponse createOrder(@Validated @RequestBody CreateOrderRequest createOrderRequest) {
        FlightOrder order = bookFlightService.createOrder(
                createOrderRequest.getCardNo(), createOrderRequest.getFlightNo()
        );

        CreateOrderResponse createOrderResponse = new CreateOrderResponse();
        createOrderResponse.setAmount(order.getAmount());
        createOrderResponse.setCardNo(order.getCardNo());
        createOrderResponse.setPaymentUrl(order.getPaymentUrl());
        createOrderResponse.setId(order.getId());
        return createOrderResponse;
    }

    @GetMapping("/orders/{id}")
    public FlightOrderDetailResponse getOrderDetail(@PathVariable Long id) {
        FlightOrder flightOrder = bookFlightService.getOrderDetail(id);
        FlightOrderDetailResponse detailResponse = new FlightOrderDetailResponse();

        detailResponse.setFlightNo(flightOrder.getFlightNo());
        detailResponse.setAmount(flightOrder.getAmount());
        detailResponse.setPosition(flightOrder.getPosition());
        detailResponse.setCardNo(flightOrder.getCardNo());
        detailResponse.setStatus(flightOrder.getStatus());
        detailResponse.setReservePositionId(flightOrder.getReservePositionId());
        detailResponse.setPaymentId(flightOrder.getPaymentId());
        detailResponse.setId(flightOrder.getId());
        return detailResponse;
    }
}
