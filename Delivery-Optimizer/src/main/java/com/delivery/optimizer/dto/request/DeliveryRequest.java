package com.delivery.optimizer.dto.request;

import com.delivery.optimizer.dto.Location;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeliveryRequest {

    @NotNull(message = "Executive start location is required")
    private Location startLocation;
    
    @NotEmpty(message = "Order list cannot be empty")
    @Valid
    private List<OrderRequest> orders;

}