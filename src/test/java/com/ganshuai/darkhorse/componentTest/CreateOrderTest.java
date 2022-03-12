package com.ganshuai.darkhorse.componentTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.ganshuai.darkhorse.client.PaymentClient;
import com.ganshuai.darkhorse.client.ShippingClient;
import com.ganshuai.darkhorse.client.response.PaymentResponse;
import com.ganshuai.darkhorse.client.response.ReservePositionResponse;
import com.ganshuai.darkhorse.controller.BookFlightController;
import com.ganshuai.darkhorse.controller.request.CreateOrderRequest;
import com.ganshuai.darkhorse.controller.response.CreateOrderResponse;
import com.ganshuai.darkhorse.exceptions.ApplicationError;
import com.ganshuai.darkhorse.model.FlightOrderStatus;
import com.ganshuai.darkhorse.repository.FlightOrderRepository;
import com.ganshuai.darkhorse.repository.entity.FlightOrderEntity;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class CreateOrderTest {
    @MockBean
    private ShippingClient shippingClient;
    @MockBean
    private PaymentClient paymentClient;

    @Autowired
    private FlightOrderRepository repository;

    @Autowired
    private BookFlightController bookFlightController;

    @Test
    void should_throw_error_when_shipping_service_error() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setFlightNo("H-20");
        createOrderRequest.setCardNo("123456789012345678");

        doThrow(new ApplicationError(HttpStatus.BAD_REQUEST, "Shipping-40001", "机票已售罄"))
                .when(shippingClient).reservePosition("123456789012345678", "H-20");

        try {
            bookFlightController.createOrder(createOrderRequest);
        } catch (ApplicationError error) {
            assertThat(error.getCode()).isEqualTo("Shipping-40001");
        }
    }

    @Test
    void should_throw_http_client_error_exception_when_payment_error() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setFlightNo("H-21");
        createOrderRequest.setCardNo("123456789012345678");

        ReservePositionResponse reservePositionResponse = new ReservePositionResponse();
        reservePositionResponse.setPosition("18A");
        reservePositionResponse.setAmount(1.1F);
        reservePositionResponse.setFlightNo("H-21");
        reservePositionResponse.setCardNo("123456789012345678");
        when(shippingClient.reservePosition("123456789012345678", "H-21")).thenReturn(reservePositionResponse);

        doThrow(new ApplicationError(HttpStatus.GATEWAY_TIMEOUT, "BookFlight-50001", "服务错误"))
                .when(paymentClient).createOrder(any());
        try {
            bookFlightController.createOrder(createOrderRequest);
        } catch (ApplicationError error) {
            assertThat(error.getCode()).isEqualTo("BookFlight-50001");
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

        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setFlightNo("H-21");
        createOrderRequest.setCardNo("123456789012345678");
        CreateOrderResponse result = bookFlightController.createOrder(createOrderRequest);
        Optional<FlightOrderEntity> entity = repository.findById(result.getId());

        assertThat(entity).isNotEqualTo(Optional.empty());
        assertThat(result.getAmount()).isEqualTo(reservePositionResponse.getAmount());
        assertThat(result.getCardNo()).isEqualTo(createOrderRequest.getCardNo());
        assertThat(result.getPaymentUrl()).isEqualTo(paymentResponse.getPaymentUrl());

        if (entity.isPresent()) {
            FlightOrderEntity order = entity.get();
            assertThat(paymentResponse.getId()).isEqualTo(order.getPaymentId());
            assertThat(order.getStatus()).isEqualTo(FlightOrderStatus.WAIT_PAYMENT);
            assertThat(order.getReservePositionId()).isEqualTo(reservePositionResponse.getId());
            assertThat(order.getAmount()).isEqualTo(reservePositionResponse.getAmount());
            assertThat(order.getFlightNo()).isEqualTo(reservePositionResponse.getFlightNo());
            assertThat(order.getCardNo()).isEqualTo(reservePositionResponse.getCardNo());
            assertThat(order.getPosition()).isEqualTo(reservePositionResponse.getPosition());
        }
    }
}
