package com.graysenko.NewPathBackEnd.Entities.Order;

import com.graysenko.NewPathBackEnd.Entities.Item.Item;
import com.graysenko.NewPathBackEnd.Entities.User.User;
import com.graysenko.NewPathBackEnd.Enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderItem> orderItem;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "order_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp orderDate;

    private String stripeSessionId;
    private Double totalAmount;

    @Column(name = "applied_promocode")
    private String appliedPromocode;

    public Order(User user, List<OrderItem> orderItem, OrderStatus orderStatus, Timestamp orderDate, String stripeSessionId, Double totalAmount, String appliedPromocode) {
        this.user = user;
        this.orderItem = orderItem;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.stripeSessionId = stripeSessionId;
        this.totalAmount = totalAmount;
        this.appliedPromocode = appliedPromocode;
    }
}
