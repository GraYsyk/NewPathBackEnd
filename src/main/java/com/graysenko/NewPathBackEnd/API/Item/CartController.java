package com.graysenko.NewPathBackEnd.API.Item;

import com.graysenko.NewPathBackEnd.DTOs.User.CartItemDTO;
import com.graysenko.NewPathBackEnd.Entities.CartItem;
import com.graysenko.NewPathBackEnd.Entities.Default.Settings;
import com.graysenko.NewPathBackEnd.Entities.Item.Item;
import com.graysenko.NewPathBackEnd.Entities.Item.ProductVariant;
import com.graysenko.NewPathBackEnd.Entities.User.User;
import com.graysenko.NewPathBackEnd.Repositories.Item.CartItemRepository;
import com.graysenko.NewPathBackEnd.Services.Default.SettingsService;
import com.graysenko.NewPathBackEnd.Services.Item.CartService;
import com.graysenko.NewPathBackEnd.Services.Item.ItemService;
import com.graysenko.NewPathBackEnd.Services.User.UserService;
import com.graysenko.NewPathBackEnd.exceptions.AppError;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final SettingsService settingsService;
    private final UserService userService;
    private final ItemService itemService;

    @GetMapping("")
    public ResponseEntity<?> getCartItems(@RequestHeader("Authorization") String authHeader) {
        User user = null;
        try {
            user = getVerifiedUserFromToken(authHeader);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(cartService.findAll(user.getEmail()));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCartItem(@RequestBody CartItemDTO cartItemDTO,
                                         @RequestHeader("Authorization") String authHeader) {
        Settings settings = settingsService.getSettings();
        if (!settings.isCollectionVisible()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AppError(403, "Collection is not available yet"));
        }

        try {
            User user = getVerifiedUserFromToken(authHeader);
            Item item = itemService.findById(cartItemDTO.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            ProductVariant variant = itemService.findVariant(item,
                    cartItemDTO.getSize(),
                    cartItemDTO.getColor());

            int alreadyInCart = cartItemRepository
                    .findAllByUserEmail(user.getEmail())
                    .stream()
                    .filter(ci -> ci.getItem().getId().equals(item.getId())
                            && ci.getSize().equals(cartItemDTO.getSize())
                            && ci.getColor().equals(cartItemDTO.getColor()))
                    .mapToInt(CartItem::getQuantity)
                    .sum();

            if (alreadyInCart + cartItemDTO.getQuantity() > variant.getQuantity()) {
                return ResponseEntity.badRequest()
                        .body(new AppError(400, "Not enough items in stock"));
            }

            // Если такой вариант уже есть в корзине — увеличиваем quantity
            cartItemRepository.findAllByUserEmail(user.getEmail())
                    .stream()
                    .filter(ci -> ci.getItem().getId().equals(item.getId())
                            && ci.getSize().equals(cartItemDTO.getSize())
                            && ci.getColor().equals(cartItemDTO.getColor()))
                    .findFirst()
                    .ifPresentOrElse(
                            existing -> {
                                existing.setQuantity(existing.getQuantity() + cartItemDTO.getQuantity());
                                cartItemRepository.save(existing);
                            },
                            () -> {
                                CartItem newItem = new CartItem(user, item,
                                        cartItemDTO.getSize(),
                                        cartItemDTO.getColor(),
                                        cartItemDTO.getQuantity());
                                cartItemRepository.save(newItem);
                            }
                    );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long id,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = getVerifiedUserFromToken(authHeader);
            cartService.removeCartItem( id, user.getEmail());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(400, e.getMessage()));
        }
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId,
                                            @RequestBody CartItemDTO cartItemDTO,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = getVerifiedUserFromToken(authHeader);
            cartService.updateQuantity(cartItemId, cartItemDTO.getQuantity(), user.getEmail());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(400, e.getMessage()));
        }
    }

    private User getVerifiedUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Token is empty");

        String token = authHeader.substring(7);
        User user = userService.getUserFromToken(token);

        if (user == null) throw new RuntimeException("Invalid token");
        return user;
    }
}
