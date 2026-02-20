package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackingUpdateDto {

    private Long orderId;
    private String trackingNumber;
    private String courier;
}
