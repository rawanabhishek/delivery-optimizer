package com.delivery.optimizer.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DeliveryRouteResponse {
    private double totalTimeMinutes;
    private List<String> optimalPath;
}