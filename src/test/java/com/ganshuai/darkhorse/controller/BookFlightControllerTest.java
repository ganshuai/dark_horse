package com.ganshuai.darkhorse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.ganshuai.darkhorse.client.response.FlightOrderDetailResponse;
import com.ganshuai.darkhorse.controller.request.CreateOrderRequest;
import com.ganshuai.darkhorse.controller.response.CreateOrderResponse;
import com.ganshuai.darkhorse.exceptions.ApplicationError;
import com.ganshuai.darkhorse.model.FlightOrder;
import com.ganshuai.darkhorse.model.FlightOrderStatus;
import com.ganshuai.darkhorse.service.BookFlightService;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class BookFlightControllerTest {
    @Mock
    private BookFlightService bookFlightService;

    @InjectMocks
    private BookFlightController bookFlightController;

    @Autowired
    private Validator validator;


    @Test
    void should_throw_error_when_cardNo_not_correct() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setFlightNo("h-20");
        createOrderRequest.setCardNo("1234567890");
        Set<ConstraintViolation<CreateOrderRequest>> validate = validator.validate(createOrderRequest);
        assertThat(validate.size()).isEqualTo(1);
    }

    @Test
    void should_not_throw_error_when_cardNo_is_correct() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setFlightNo("1");
        createOrderRequest.setCardNo("123456789012345678");
        Set<ConstraintViolation<CreateOrderRequest>> validate = validator.validate(createOrderRequest);
        assertThat(validate.size()).isZero();
    }

    @Test
    void should_throw_error_when_shipping_service_error() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setFlightNo("H-20");
        createOrderRequest.setCardNo("123456789012345678");
        doThrow(new ApplicationError(HttpStatus.BAD_REQUEST, "Shipping-40001", "机票已售罄"))
                .when(bookFlightService)
                .createOrder(createOrderRequest.getCardNo(), createOrderRequest.getFlightNo());
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
        doThrow(new ApplicationError(HttpStatus.GATEWAY_TIMEOUT, "BookFlight-50001", "服务错误"))
                .when(bookFlightService)
                .createOrder(createOrderRequest.getCardNo(), createOrderRequest.getFlightNo());
        try {
            bookFlightController.createOrder(createOrderRequest);
        } catch (ApplicationError error) {
            assertThat(error.getCode()).isEqualTo("BookFlight-50001");
        }
    }

    @Test
    void should_return_create_order_response_when_create_success() {
        FlightOrder flightOrder = new FlightOrder();
        flightOrder.setPaymentUrl("https://xxx.com");
        flightOrder.setStatus(FlightOrderStatus.WAIT_PAYMENT);
        flightOrder.setAmount(1.1F);
        flightOrder.setPosition("18A");
        flightOrder.setPaymentId("P2022031109201109114");
        flightOrder.setId(1L);
        flightOrder.setReservePositionId("S2022031109201100001");
        flightOrder.setFlightNo("H-21");
        flightOrder.setCardNo("123456789012345678");

        when(bookFlightService.createOrder(flightOrder.getCardNo(), flightOrder.getFlightNo())).thenReturn(flightOrder);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setFlightNo(flightOrder.getFlightNo());
        request.setCardNo(flightOrder.getCardNo());

        CreateOrderResponse order = bookFlightController.createOrder(request);
        assertThat(order.getAmount()).isEqualTo(flightOrder.getAmount());
        assertThat(order.getId()).isEqualTo(flightOrder.getId());
        assertThat(order.getCardNo()).isEqualTo(flightOrder.getCardNo());
        assertThat(order.getPaymentUrl()).isEqualTo(flightOrder.getPaymentUrl());
    }

    @Test
    void should_throw_error_when_order_not_exist() {
        doThrow(new ApplicationError(HttpStatus.BAD_REQUEST, "BookFlight-40002", "订单不存在")).when(bookFlightService).getOrderDetail(1L);
        try {
            bookFlightController.getOrderDetail(1L);
        } catch (ApplicationError error) {
            assertThat(error.getCode()).isEqualTo("BookFlight-40002");
        }
    }

    @Test
    void should_return_flight_order_detail_response_when_order_exist() {
        FlightOrder flightOrder = new FlightOrder();
        flightOrder.setStatus(FlightOrderStatus.WAIT_PAYMENT);
        flightOrder.setAmount(1.1F);
        flightOrder.setPosition("18A");
        flightOrder.setPaymentId("P2022031109201109114");
        flightOrder.setId(1L);
        flightOrder.setReservePositionId("S2022031109201100001");
        flightOrder.setFlightNo("H-21");
        flightOrder.setCardNo("123456789012345678");

        when(bookFlightService.getOrderDetail(1L)).thenReturn(flightOrder);
        FlightOrderDetailResponse orderDetail = bookFlightController.getOrderDetail(1L);

        assertThat(orderDetail.getStatus()).isEqualTo(flightOrder.getStatus());
        assertThat(orderDetail.getAmount()).isEqualTo(flightOrder.getAmount());
        assertThat(orderDetail.getPosition()).isEqualTo(flightOrder.getPosition());
        assertThat(orderDetail.getPaymentId()).isEqualTo(flightOrder.getPaymentId());
        assertThat(orderDetail.getId()).isEqualTo(flightOrder.getId());
        assertThat(orderDetail.getReservePositionId()).isEqualTo(flightOrder.getReservePositionId());
        assertThat(orderDetail.getFlightNo()).isEqualTo(flightOrder.getFlightNo());
        assertThat(orderDetail.getCardNo()).isEqualTo(flightOrder.getCardNo());
    }
}
