package com.ganshuai.darkhorse.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotBlank
    private String flightNo;

    @Pattern(regexp = "^\\d{18}$")
    @NotBlank
    private String cardNo;
}
