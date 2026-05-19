package com.graysenko.NewPathBackEnd.Entities.Order;

import com.graysenko.NewPathBackEnd.Entities.Item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "size")
    private String size;
    @Column(name = "color")
    private String color;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "priceAtPurchase")
    private Double priceAtPurchase;

    public OrderItem(Order order, Item item, String size, String color, Integer quantity, Double priceAtPurchase) {
        this.order = order;
        this.item = item;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }
}
