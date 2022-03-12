package com.ganshuai.darkhorse.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.ganshuai.darkhorse.client.response.ReservePositionResponse;
import com.ganshuai.darkhorse.exceptions.ApplicationError;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
class ShippingClientTest {
    @Autowired
    private ShippingClient shippingClient;

//    @Ignore
//    @Test
    void should_throw_error_when_position_not_sold_out() {
        try {
            shippingClient.reservePosition("123456789012345678", "H-20");
        } catch (ApplicationError error) {
            assertThat(error.getCode()).isEqualTo("Shipping-40001");
        }
    }

//    @Ignore
//    @Test
    void should_return_position_detail_when_position_not_sold_out() {
        ReservePositionResponse reservePositionResponse = shippingClient.reservePosition("123456789012345678", "H-21");

        assertThat(reservePositionResponse).isNotNull();
        assertThat(reservePositionResponse.getId()).isNotBlank();
        assertThat(reservePositionResponse.getCardNo()).isNotBlank();
        assertThat(reservePositionResponse.getFlightNo()).isNotBlank();
        assertThat(reservePositionResponse.getPosition()).isNotBlank();
        assertThat(reservePositionResponse.getAmount()).isNotNull();
    }
}
