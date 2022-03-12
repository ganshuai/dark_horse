package com.ganshuai.darkhorse.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.ganshuai.darkhorse.client.PaymentClient;
import com.ganshuai.darkhorse.client.ShippingClient;
import com.ganshuai.darkhorse.client.response.PaymentResponse;
import com.ganshuai.darkhorse.client.response.ReservePositionResponse;
import com.ganshuai.darkhorse.exceptions.ApplicationError;
import com.ganshuai.darkhorse.model.FlightOrder;
import com.ganshuai.darkhorse.model.FlightOrderStatus;
import com.ganshuai.darkhorse.repository.FlightOrderRepository;
import com.ganshuai.darkhorse.repository.entity.FlightOrderEntity;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
class BookFlightServiceTest {
    @Mock
    private ShippingClient shippingClient;
    @Mock
    private PaymentClient paymentClient;

    @Autowired
    private FlightOrderRepository flightOrderRepository;

    private BookFlightService bookFlightService;

    @PostConstruct
    public void init() {
        bookFlightService = new BookFlightService(shippingClient, paymentClient, flightOrderRepository);
    }


    @Test
    void should_throw_error_when_shipping_error() {
        doThrow(new ApplicationError(HttpStatus.BAD_REQUEST, "Shipping-40001", "机票已售罄"))
                .when(shippingClient).reservePosition("123456789012345678", "H-20");
        try {
            bookFlightService.createOrder("123456789012345678", "H-20");
        } catch (ApplicationError error) {
            assertThat(error.getCode()).isEqualTo("Shipping-40001");
        }
    }

    @Test
    void should_throw_error_when_payment_error() {
        ReservePositionResponse reservePositionResponse = new ReservePositionResponse();
        reservePositionResponse.setPosition("18A");
        reservePositionResponse.setAmount(1.1F);
        reservePositionResponse.setFlightNo("H-21");
        reservePositionResponse.setCardNo("123456789012345678");
        when(shippingClient.reservePosition("123456789012345678", "H-21")).thenReturn(reservePositionResponse);

        when(paymentClient.createOrder(any())).thenThrow(new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT));
        try {
            bookFlightService.createOrder("123456789012345678", "H-21");
        } catch (HttpClientErrorException error) {
            assertThat(error.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        }
    }

    @Test
    void should_return_flight_order_detail_when_create_success() {
        ReservePositionResponse reservePositionResponse = new ReservePositionResponse();
        reservePositionResponse.setPosition("18A");
        reservePositionResponse.setAmount(1.1f);
        reservePositionResponse.setFlightNo("H-21");
        reservePositionResponse.setCardNo("123456789012345678");
        reservePositionResponse.setId("S2022031109201100001");
        when(shippingClient.reservePosition("123456789012345678", "H-21")).thenReturn(reservePositionResponse);

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setAmount(reservePositionResponse.getAmount());
        paymentResponse.setCardNo(reservePositionResponse.getCardNo());
        paymentResponse.setId("P2022031109201109114");
        paymentResponse.setPaymentUrl("https://xxx.com");
        when(paymentClient.createOrder(any())).thenReturn(paymentResponse);

        FlightOrderEntity entity = new FlightOrderEntity();
        entity.setPaymentId(paymentResponse.getId());
        entity.setStatus(FlightOrderStatus.WAIT_PAYMENT);
        entity.setReservePositionId(reservePositionResponse.getId());
        entity.setAmount(reservePositionResponse.getAmount());
        entity.setFlightNo(reservePositionResponse.getFlightNo());
        entity.setCardNo(reservePositionResponse.getCardNo());
        entity.setPosition(reservePositionResponse.getPosition());
        FlightOrder result = bookFlightService.createOrder(reservePositionResponse.getCardNo(), reservePositionResponse.getFlightNo());

        assertThat(result.getFlightNo()).isEqualTo(entity.getFlightNo());
        assertThat(result.getAmount()).isEqualTo(entity.getAmount());
        assertThat(result.getPosition()).isEqualTo(entity.getPosition());
        assertThat(result.getCardNo()).isEqualTo(entity.getCardNo());
        assertThat(result.getReservePositionId()).isEqualTo(entity.getReservePositionId());
        assertThat(result.getStatus()).isEqualTo(entity.getStatus());
        assertThat(result.getPaymentId()).isEqualTo(entity.getPaymentId());
        assertThat(result.getPaymentUrl()).isEqualTo(paymentResponse.getPaymentUrl());
    }

    @Test
    void should_throw_error_when_order_not_exist() {
        try {
            bookFlightService.getOrderDetail(Long.MAX_VALUE);
        } catch (ApplicationError error) {
            assertThat(error.getCode()).isEqualTo("BookFlight-40002");
        }
    }

    @Test
    void should_return_flight_order_detail_when_save_order() {
        FlightOrderEntity entity = new FlightOrderEntity();
        entity.setPaymentId("P2022031109201109114");
        entity.setStatus(FlightOrderStatus.WAIT_PAYMENT);
        entity.setReservePositionId("S2022031109201100001");
        entity.setAmount(1.1F);
        entity.setFlightNo("H-21");
        entity.setCardNo("123456789012345678");
        entity.setPosition("18A");
        FlightOrderEntity saveData = flightOrderRepository.save(entity);

        FlightOrder orderDetail = bookFlightService.getOrderDetail(saveData.getId());

        assertThat(entity.getPaymentId()).isEqualTo(orderDetail.getPaymentId());
        assertThat(entity.getStatus()).isEqualTo(orderDetail.getStatus());
        assertThat(entity.getReservePositionId()).isEqualTo(orderDetail.getReservePositionId());
        assertThat(entity.getAmount()).isEqualTo(orderDetail.getAmount());
        assertThat(entity.getFlightNo()).isEqualTo(orderDetail.getFlightNo());
        assertThat(entity.getCardNo()).isEqualTo(orderDetail.getCardNo());
        assertThat(entity.getPosition()).isEqualTo(orderDetail.getPosition());
    }
}
