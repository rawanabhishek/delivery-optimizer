package com.delivery.optimizer.dto.request;

import com.delivery.optimizer.dto.Location;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @NotNull(message = "Order ID is required")
    private String orderId;
    
    @NotNull(message = "Restaurant location is required")
    private Location restaurantLocation;
    
    @NotNull(message = "Consumer location is required")
    private Location consumerLocation;
    
    // Time in minutes from the moment batch is accepted
    private double preparationTime; 
}