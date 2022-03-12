package com.ganshuai.darkhorse.componentTest;

import static org.assertj.core.api.Assertions.assertThat;

import com.ganshuai.darkhorse.client.response.FlightOrderDetailResponse;
import com.ganshuai.darkhorse.controller.BookFlightController;
import com.ganshuai.darkhorse.exceptions.ApplicationError;
import com.ganshuai.darkhorse.model.FlightOrderStatus;
import com.ganshuai.darkhorse.repository.FlightOrderRepository;
import com.ganshuai.darkhorse.repository.entity.FlightOrderEntity;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class GetOrderDetailTest {
    @Autowired
    private FlightOrderRepository repository;

    @Autowired
    private BookFlightController bookFlightController;

    @Test
    void should_throw_error_when_order_not_exist() {
        try {
            bookFlightController.getOrderDetail(Long.MAX_VALUE);
            throw new ApplicationError(HttpStatus.BAD_REQUEST, "", "");
        } catch (ApplicationError error) {
            assertThat(error.getCode()).isEqualTo("BookFlight-40002");
        }
    }

    @Test
    void should_return_order_detail_when_order_exist() {
        FlightOrderEntity flightOrderEntity = new FlightOrderEntity();
        flightOrderEntity.setCardNo("123456789012345678");
        flightOrderEntity.setFlightNo("H-21");
        flightOrderEntity.setAmount(1.1F);
        flightOrderEntity.setStatus(FlightOrderStatus.WAIT_PAYMENT);
        flightOrderEntity.setPosition("18A");
        flightOrderEntity.setPaymentId("P2022031109201101114");
        flightOrderEntity.setReservePositionId("S2022031109201101001");

        repository.save(flightOrderEntity);

        FlightOrderDetailResponse orderDetail = bookFlightController.getOrderDetail(flightOrderEntity.getId());
        assertThat(orderDetail.getId()).isEqualTo(flightOrderEntity.getId());
        assertThat(orderDetail.getCardNo()).isEqualTo(flightOrderEntity.getCardNo());
        assertThat(orderDetail.getFlightNo()).isEqualTo(flightOrderEntity.getFlightNo());
        assertThat(orderDetail.getAmount()).isEqualTo(flightOrderEntity.getAmount());
        assertThat(orderDetail.getStatus()).isEqualTo(flightOrderEntity.getStatus());
        assertThat(orderDetail.getPosition()).isEqualTo(flightOrderEntity.getPosition());
        assertThat(orderDetail.getPaymentId()).isEqualTo(flightOrderEntity.getPaymentId());
        assertThat(orderDetail.getReservePositionId()).isEqualTo(flightOrderEntity.getReservePositionId());

    }
}
