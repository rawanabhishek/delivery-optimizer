package com.delivery.optimizer.contoller;


import com.delivery.optimizer.dto.request.DeliveryRequest;
import com.delivery.optimizer.dto.response.DeliveryRouteResponse;
import com.delivery.optimizer.service.RouteOptimizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    @Autowired
    private  RouteOptimizationService routeService;

    @PostMapping("/optimize-route")
    public ResponseEntity<DeliveryRouteResponse> getBestRoute(@Valid @RequestBody DeliveryRequest request) {
        return ResponseEntity.ok(routeService.findBestRoute(request));
    }
}