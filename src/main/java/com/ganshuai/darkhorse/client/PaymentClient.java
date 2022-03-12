package com.ganshuai.darkhorse.client;


import com.ganshuai.darkhorse.client.request.PaymentRequest;
import com.ganshuai.darkhorse.client.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PaymentClient {
    private static final String CREATE_ORDER = "http://payment.domain.com/orders";
    private final RestTemplate restTemplate;

    public PaymentResponse createOrder(PaymentRequest request) {
        return restTemplate.exchange(
                CREATE_ORDER,
                HttpMethod.POST,
                new HttpEntity<>(request),
                PaymentResponse.class
        ).getBody();
    }
}
