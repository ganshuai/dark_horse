package com.ganshuai.darkhorse.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.ganshuai.darkhorse.client.request.PaymentRequest;
import com.ganshuai.darkhorse.client.response.PaymentResponse;
import java.util.UUID;
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
class PaymentClientTest {
    @Autowired
    private PaymentClient paymentClient;

//    @Test
//    @Ignore
    void should_return_order_detail_when_order_create_success() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setId("P2022031109201109114");
        paymentRequest.setAmount(1.1F);
        paymentRequest.setCardNo("123456789012345678");

        PaymentResponse order = paymentClient.createOrder(paymentRequest);

        assertThat(order).isNotNull();
        assertThat(order.getPaymentUrl()).isNotBlank();
        assertThat(order.getId()).isNotBlank();
        assertThat(order.getAmount()).isNotNull();
        assertThat(order.getCardNo()).isNotBlank();
    }
}
