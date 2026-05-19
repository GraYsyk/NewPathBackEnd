package com.graysenko.NewPathBackEnd.Services.Order;

import com.graysenko.NewPathBackEnd.DTOs.Order.OrderDTO;
import com.graysenko.NewPathBackEnd.DTOs.Order.OrderItemsDTO;
import com.graysenko.NewPathBackEnd.Entities.CartItem;
import com.graysenko.NewPathBackEnd.Entities.Default.Promocode;
import com.graysenko.NewPathBackEnd.Entities.Item.ProductVariant;
import com.graysenko.NewPathBackEnd.Entities.Order.Order;
import com.graysenko.NewPathBackEnd.Entities.Order.OrderItem;
import com.graysenko.NewPathBackEnd.Entities.User.User;
import com.graysenko.NewPathBackEnd.Enums.OrderStatus;
import com.graysenko.NewPathBackEnd.Repositories.Order.OrderRepository;
import com.graysenko.NewPathBackEnd.Services.Default.SettingsService;
import com.graysenko.NewPathBackEnd.Services.Item.ItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderService {
    private final OrderRepository orderRepository;
    private final SettingsService settingsService;
    private final ItemService itemService;

    @Transactional
    public OrderDTO createOrder(List<CartItem> cartItems, String stripeSessionId, String promocode) {
        if (cartItems == null || cartItems.isEmpty()) return null;
        //Creating orders

        Promocode promo = settingsService.checkPromocode(promocode);
        int discountPercent = promo != null ? promo.getDiscountPercent() : 0;

        Order order = new Order();
        order.setUser(cartItems.getFirst().getUser());
        order.setOrderStatus(OrderStatus.PAID);
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setStripeSessionId(stripeSessionId);
        if (promo != null) order.setAppliedPromocode(promocode);

        double total = cartItems.stream()
                .mapToDouble(c -> c.getItem().getPrice() * c.getQuantity() * (1 - discountPercent / 100.0))
                .sum();
        order.setTotalAmount(total);

        List<OrderItem> orderItems = cartItems.stream()
                        .map(cartItem ->
                                new OrderItem(order, cartItem.getItem(), cartItem.getSize(),
                                        cartItem.getColor(), cartItem.getQuantity(), cartItem.getItem().getPrice()
                                )).toList();

        order.setOrderItem(orderItems);
        orderRepository.save(order);

        cartItems.forEach(cartItem -> {
            ProductVariant variant = itemService.findVariant(
                    cartItem.getItem(), cartItem.getSize(), cartItem.getColor()
            );
            itemService.sellVariants(variant, cartItem.getQuantity(), cartItem.getItem());
        });

        //Creating back info

        return toDTO(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    @Transactional
    public void save(@NonNull Order order) {
        orderRepository.save(order);
    }

    public List<OrderDTO> findAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::toDTO).toList();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public OrderDTO toDTO(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getUser().getEmail(),
                order.getOrderItem().stream()
                        .map(orderItem -> new OrderItemsDTO(
                                orderItem.getItem().getId(),
                                orderItem.getItem().getName(),
                                orderItem.getItem().getFrontImage(),
                                orderItem.getSize(),
                                orderItem.getColor(),
                                orderItem.getQuantity(),
                                orderItem.getPriceAtPurchase()
                        )).toList(),
                order.getOrderStatus(),
                order.getOrderDate(),
                order.getTotalAmount()
        );
    }

    public List<OrderDTO> getUserOrders(User user) {
        return orderRepository.findAllByUser(user).stream()
                .map(this::toDTO)
                .toList();
    }
}
