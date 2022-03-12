package com.ganshuai.darkhorse.client;

import com.ganshuai.darkhorse.client.request.ReservePositionRequest;
import com.ganshuai.darkhorse.client.response.ReservePositionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ShippingClient {
    private static final String RESERVE_POSITION_URL = "http://shipping.domain.com/orders";
    private final RestTemplate restTemplate;

    public ReservePositionResponse reservePosition(String cardNo, String flightNo) {
        ReservePositionRequest request = new ReservePositionRequest();
        request.setCardNo(cardNo);
        request.setFlightNo(flightNo);
        return restTemplate.exchange(
                RESERVE_POSITION_URL,
                HttpMethod.POST,
                new HttpEntity<>(request),
                ReservePositionResponse.class
        ).getBody();
    }
}
