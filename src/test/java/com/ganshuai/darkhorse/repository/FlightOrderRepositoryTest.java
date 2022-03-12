package com.ganshuai.darkhorse.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ganshuai.darkhorse.model.FlightOrderStatus;
import com.ganshuai.darkhorse.repository.entity.FlightOrderEntity;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
class FlightOrderRepositoryTest {
    @Autowired
    private FlightOrderRepository repository;

    @Test
    public void should_return_empty_when_order_not_exist() {
        Optional<FlightOrderEntity> result = repository.findById(Long.MAX_VALUE);
        assertThat(result.isPresent()).isEqualTo(false);
    }

    @Test
    public void should_return_order_detail_when_order_exist() {
        FlightOrderEntity entity = new FlightOrderEntity();
        entity.setPaymentId("P2022031109201109114");
        entity.setStatus(FlightOrderStatus.WAIT_PAYMENT);
        entity.setReservePositionId("S2022031109201100001");
        entity.setAmount(1.1F);
        entity.setFlightNo("H-21");
        entity.setCardNo("123456789012345678");
        entity.setPosition("18A");
        FlightOrderEntity saveData = repository.save(entity);

        Optional<FlightOrderEntity> result = repository.findById(entity.getId());
        assertThat(result.isPresent()).isEqualTo(true);

        if (result.isPresent()) {
            FlightOrderEntity order = result.get();
            assertThat(saveData.getId()).isEqualTo(order.getId());
            assertThat(entity.getPaymentId()).isEqualTo(order.getPaymentId());
            assertThat(entity.getStatus()).isEqualTo(order.getStatus());
            assertThat(entity.getReservePositionId()).isEqualTo(order.getReservePositionId());
            assertThat(entity.getAmount()).isEqualTo(order.getAmount());
            assertThat(entity.getFlightNo()).isEqualTo(order.getFlightNo());
            assertThat(entity.getCardNo()).isEqualTo(order.getCardNo());
            assertThat(entity.getPosition()).isEqualTo(order.getPosition());
        }
    }
}
