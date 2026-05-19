package com.graysenko.NewPathBackEnd.Entities;

import com.graysenko.NewPathBackEnd.Entities.Item.Item;
import com.graysenko.NewPathBackEnd.Entities.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private String size;
    private String color;
    private Integer quantity;

    public CartItem(User user, Item item, String size, String color, Integer quantity) {
        this.user = user;
        this.item = item;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
    }
}
