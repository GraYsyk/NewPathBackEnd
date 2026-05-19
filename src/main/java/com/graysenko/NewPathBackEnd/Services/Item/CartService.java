package com.graysenko.NewPathBackEnd.Services.Item;

import com.graysenko.NewPathBackEnd.DTOs.User.CartItemDTO;
import com.graysenko.NewPathBackEnd.Entities.CartItem;
import com.graysenko.NewPathBackEnd.Entities.Item.ProductVariant;
import com.graysenko.NewPathBackEnd.Repositories.Item.CartItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ItemService itemService;


    public List<CartItemDTO> findAll(String email) {
        List<CartItem> cartItems = cartItemRepository.findAllByUserEmail(email);
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            cartItemDTOs.add(new CartItemDTO(
                    cartItem.getId(),
                    cartItem.getItem().getId(),
                    cartItem.getItem().getName(),
                    cartItem.getQuantity(),
                    cartItem.getItem().getProductVariant().stream()
                            .filter(v -> v.getSize().equals(cartItem.getSize())
                                    && v.getColor().equals(cartItem.getColor()))
                            .findFirst()
                            .map(ProductVariant::getQuantity)
                            .orElse(0),
                    cartItem.getSize(),
                    cartItem.getColor(),
                    cartItem.getItem().getPrice(),
                    cartItem.getItem().getFrontImage()
            ));
        }
        return cartItemDTOs;
    }

    public void removeCartItem(Long cartItemId, String email) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getUser().getEmail().equals(email))
            throw new RuntimeException("Access denied");

        cartItemRepository.delete(cartItem);
    }

    public void updateQuantity(Long cartItemId, Integer quantity, String email) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getUser().getEmail().equals(email))
            throw new RuntimeException("Access denied");

        if (quantity < 1) {
            cartItemRepository.delete(cartItem);
            return;
        }

        ProductVariant variant = itemService.findVariant(
                cartItem.getItem(), cartItem.getSize(), cartItem.getColor()
        );
        if (quantity > variant.getQuantity()) {
            throw new RuntimeException("Not enough items in stock");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }
}
