package com.ganshuai.darkhorse.service;

import com.ganshuai.darkhorse.client.PaymentClient;
import com.ganshuai.darkhorse.client.ShippingClient;
import com.ganshuai.darkhorse.client.request.PaymentRequest;
import com.ganshuai.darkhorse.client.response.PaymentResponse;
import com.ganshuai.darkhorse.client.response.ReservePositionResponse;
import com.ganshuai.darkhorse.exceptions.ApplicationError;
import com.ganshuai.darkhorse.model.FlightOrder;
import com.ganshuai.darkhorse.model.FlightOrderStatus;
import com.ganshuai.darkhorse.model.PaymentOrder;
import com.ganshuai.darkhorse.model.ReservePosition;
import com.ganshuai.darkhorse.repository.FlightOrderRepository;
import com.ganshuai.darkhorse.repository.entity.FlightOrderEntity;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookFlightService {
    private final ShippingClient shippingClient;
    private final PaymentClient paymentClient;
    private final FlightOrderRepository flightOrderRepository;

    public FlightOrder createOrder(String cardNo, String flightNo) {
        ReservePosition reservePosition = reservePosition(cardNo, flightNo);
        PaymentOrder paymentOrder = createPaymentOrder(cardNo, reservePosition.getAmount());
        FlightOrder flightOrder = getFlightOrder(cardNo, reservePosition, paymentOrder);

        return saveFlightOrder(flightOrder);
    }

    private FlightOrder getFlightOrder(final String cardNo, final ReservePosition reservePosition, final PaymentOrder paymentOrder) {
        FlightOrder flightOrder = new FlightOrder();
        flightOrder.setFlightNo(reservePosition.getFlightNo());
        flightOrder.setAmount(reservePosition.getAmount());
        flightOrder.setPosition(reservePosition.getPosition());
        flightOrder.setCardNo(cardNo);
        flightOrder.setStatus(FlightOrderStatus.WAIT_PAYMENT);
        flightOrder.setReservePositionId(reservePosition.getId());
        flightOrder.setPaymentId(paymentOrder.getId());
        flightOrder.setPaymentUrl(paymentOrder.getPaymentUrl());
        return flightOrder;
    }

    private FlightOrder saveFlightOrder(FlightOrder flightOrder) {
        FlightOrderEntity flightOrderEntity = new FlightOrderEntity();
        flightOrderEntity.setFlightNo(flightOrder.getFlightNo());
        flightOrderEntity.setAmount(flightOrder.getAmount());
        flightOrderEntity.setPosition(flightOrder.getPosition());
        flightOrderEntity.setCardNo(flightOrder.getCardNo());
        flightOrderEntity.setStatus(FlightOrderStatus.WAIT_PAYMENT);
        flightOrderEntity.setReservePositionId(flightOrder.getReservePositionId());
        flightOrderEntity.setPaymentId(flightOrder.getPaymentId());
        FlightOrderEntity saveResult = flightOrderRepository.save(flightOrderEntity);
        flightOrder.setId(saveResult.getId());

        return flightOrder;
    }

    private PaymentOrder createPaymentOrder(final String cardNo, Float amount) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setId(UUID.randomUUID().toString());
        paymentRequest.setCardNo(cardNo);
        paymentRequest.setAmount(amount);
        PaymentResponse order = paymentClient.createOrder(paymentRequest);

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setPaymentUrl(order.getPaymentUrl());
        paymentOrder.setCardNo(order.getCardNo());
        paymentOrder.setAmount(order.getAmount());
        paymentOrder.setId(order.getId());
        return paymentOrder;
    }

    private ReservePosition reservePosition(String cardNo, String flightNo) {
        ReservePositionResponse response = shippingClient.reservePosition(cardNo, flightNo);
        ReservePosition reservePosition = new ReservePosition();
        reservePosition.setPosition(response.getPosition());
        reservePosition.setFlightNo(response.getFlightNo());
        reservePosition.setCardNo(response.getCardNo());
        reservePosition.setAmount(response.getAmount());
        reservePosition.setId(response.getId());

        return reservePosition;
    }

    public FlightOrder getOrderDetail(final Long id) {
        return findOrderById(id);
    }

    public FlightOrder findOrderById(Long id) {
        Optional<FlightOrderEntity> flightOrderEntity = flightOrderRepository.findById(id);
        flightOrderEntity.orElseThrow(() -> new ApplicationError(HttpStatus.BAD_REQUEST, "BookFlight-40002", "订单不存在"));
        FlightOrderEntity entity = flightOrderEntity.get();

        FlightOrder flightOrder = new FlightOrder();
        flightOrder.setFlightNo(entity.getFlightNo());
        flightOrder.setAmount(entity.getAmount());
        flightOrder.setPosition(entity.getPosition());
        flightOrder.setCardNo(entity.getCardNo());
        flightOrder.setStatus(entity.getStatus());
        flightOrder.setReservePositionId(entity.getReservePositionId());
        flightOrder.setPaymentId(entity.getPaymentId());
        flightOrder.setId(entity.getId());

        return flightOrder;
    }
}
