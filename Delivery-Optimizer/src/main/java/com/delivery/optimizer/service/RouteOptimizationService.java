package com.delivery.optimizer.service;


import com.delivery.optimizer.commons.NodeType;
import com.delivery.optimizer.dto.Location;
import com.delivery.optimizer.dto.request.DeliveryRequest;
import com.delivery.optimizer.dto.request.OrderRequest;
import com.delivery.optimizer.dto.response.DeliveryRouteResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RouteOptimizationService {

    private static final double AVERAGE_SPEED_KMH = 20.0;
    private static final int EARTH_RADIUS_KM = 6371;

    private record Node(String id, Location location, double minDepartureTime, NodeType type, String orderId) {}

    public DeliveryRouteResponse findBestRoute(DeliveryRequest request) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(new Node("Start", request.getStartLocation(), 0, NodeType.START, null));

        for (int i = 0; i < request.getOrders().size(); i++) {
            OrderRequest order = request.getOrders().get(i);
            nodes.add(new Node("R" + (i + 1), order.getRestaurantLocation(), order.getPreparationTime(), NodeType.RESTAURANT, order.getOrderId()));
            nodes.add(new Node("C" + (i + 1), order.getConsumerLocation(), 0, NodeType.CONSUMER, order.getOrderId()));
        }

        RouteResult internalResult = solveTSP(nodes);

        List<String> pathDescriptions = internalResult.path.stream()
                .map(node -> {
                    if (node.type == NodeType.START) return "Start Location";
                    return node.id + " (Order " + node.orderId + ")";
                })
                .collect(Collectors.toList());

        return DeliveryRouteResponse.builder().totalTimeMinutes(internalResult.totalTimeHours * 60).optimalPath(pathDescriptions).build();
    }


    private RouteResult solveTSP(List<Node> nodes) {
        int n = nodes.size();
        boolean[] visited = new boolean[n];
        List<Node> currentPath = new ArrayList<>();
        List<Node> bestPath = new ArrayList<>();

        double[] minTime = {Double.MAX_VALUE};

        visited[0] = true;
        currentPath.add(nodes.get(0));

        backtrack(0, 0.0, visited, currentPath, bestPath, minTime, nodes, 0);

        return new RouteResult(bestPath, minTime[0]);
    }


    private void backtrack(int currIdx, double currentTime, boolean[] visited,
                           List<Node> currentPath, List<Node> bestPath, double[] minTime,
                           List<Node> nodes, int deliveredCount) {

        // Pruning: If current time exceeds best time found, stop
        if (currentTime >= minTime[0]) return;

        // Base Case: If all delivery nodes (Type CONSUMER) are visited
        // Total nodes = 1 (start) + 2 * orders.
        // We are done when visited count == total nodes.
        if (currentPath.size() == nodes.size()) {
            if (currentTime < minTime[0]) {
                minTime[0] = currentTime;
                bestPath.clear();
                bestPath.addAll(currentPath);
            }
            return;
        }

        for (int i = 1; i < nodes.size(); i++) {
            if (!visited[i]) {
                Node nextNode = nodes.get(i);

                // Constraint Check: Can we visit this node?
                if (isValidNextMove(nextNode, visited, nodes)) {

                    double travelTime = calculateTravelTime(nodes.get(currIdx).location, nextNode.location);
                    double arrivalTime = currentTime + travelTime;

                    // Wait logic: If we arrive at R before prep is done, we wait.
                    // Departure Time = max(ArrivalTime, PrepTime)
                    // Note: Prep time applies only to Restaurants. Consumers have prepTime=0.
                    double departureTime = Math.max(arrivalTime, nextNode.minDepartureTime / 60.0); // convert mins to hours

                    visited[i] = true;
                    currentPath.add(nextNode);

                    backtrack(i, departureTime, visited, currentPath, bestPath, minTime, nodes,
                            nextNode.type == NodeType.CONSUMER ? deliveredCount + 1 : deliveredCount);

                    // Backtrack
                    currentPath.remove(currentPath.size() - 1);
                    visited[i] = false;
                }
            }
        }
    }

    private boolean isValidNextMove(Node target, boolean[] visited, List<Node> nodes) {
        if (target.type == NodeType.RESTAURANT) {
            return true; // Can always go to a restaurant if unvisited
        }
        if (target.type == NodeType.CONSUMER) {
            // Can only visit Consumer if corresponding Restaurant is visited
            for (int i = 0; i < nodes.size(); i++) {
                Node n = nodes.get(i);
                if (n.type == NodeType.RESTAURANT &&
                        n.orderId.equals(target.orderId) &&
                        visited[i]) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private double calculateTravelTime(Location l1, Location l2) {
        double distKm = haversine(l1.getLatitude(), l1.getLongitude(), l2.getLatitude(), l2.getLongitude());
        return distKm / AVERAGE_SPEED_KMH;
    }

    // Haversine formula implementation
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    // Internal helper record
    private record RouteResult(List<Node> path, double totalTimeHours) {}
}