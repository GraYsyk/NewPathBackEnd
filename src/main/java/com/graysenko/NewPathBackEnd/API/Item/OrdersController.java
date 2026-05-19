package com.graysenko.NewPathBackEnd.API.Item;

import com.graysenko.NewPathBackEnd.DTOs.Order.OrderDTO;
import com.graysenko.NewPathBackEnd.Entities.Order.Order;
import com.graysenko.NewPathBackEnd.Entities.User.User;
import com.graysenko.NewPathBackEnd.Enums.OrderStatus;
import com.graysenko.NewPathBackEnd.Services.Default.SettingsService;
import com.graysenko.NewPathBackEnd.Services.Order.OrderService;
import com.graysenko.NewPathBackEnd.Services.User.UserService;
import com.graysenko.NewPathBackEnd.exceptions.AppError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {
    private final OrderService orderService;
    private final SettingsService settingsService;
    private final UserService userService;

    //If you are reviewing the code,
    //you can find order creation end-point in Stripe Controllers section.

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(
            @RequestHeader("Authorization") String authHeader) {
        User user = getVerifiedUserFromToken(authHeader);
        List<OrderDTO> orders = orderService.getUserOrders(user).reversed();

        return ResponseEntity.ok(orders);
    }

    //ADMIN

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOrders(@RequestHeader("Authorization") String authHeader) {
        User user = getVerifiedUserFromToken(authHeader);
        List<OrderDTO> orders = orderService.findAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrder(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable Long id,
                                         @RequestParam String status) {
        User user = getVerifiedUserFromToken(authHeader);
        if (status.isEmpty()) {
            return ResponseEntity.badRequest().body(new AppError(400, "Status cannot be empty"));
        }
        Order order = orderService.findById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body(new AppError(400, "Order not found"));
        }
        switch (status.toUpperCase()) {
            case "PENDING" -> order.setOrderStatus(OrderStatus.PENDING);
            case "PAID" -> order.setOrderStatus(OrderStatus.PAID);
            case "SHIPPED" -> order.setOrderStatus(OrderStatus.SHIPPED);
            case "DELIVERED" -> order.setOrderStatus(OrderStatus.DELIVERED);
            case "CANCELED" -> order.setOrderStatus(OrderStatus.CANCELED);
            default -> {
                return ResponseEntity.badRequest().body(new AppError(400, "Status cannot be empty"));
            }
        }
        orderService.save(order);
        return ResponseEntity.ok(orderService.toDTO(order));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteOrder(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable Long id) {
        User user = getVerifiedUserFromToken(authHeader);
        Order order = orderService.findById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body(new AppError(400, "Order not found"));
        }
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
    //MISC

    private User getVerifiedUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Token is empty");

        String token = authHeader.substring(7);
        User user = userService.getUserFromToken(token);

        if (user == null) throw new RuntimeException("Invalid token");
        return user;
    }
}
