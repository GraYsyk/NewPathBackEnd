package com.graysenko.NewPathBackEnd.API.Stripe;

import com.graysenko.NewPathBackEnd.Entities.CartItem;
import com.graysenko.NewPathBackEnd.Entities.Default.Promocode;
import com.graysenko.NewPathBackEnd.Entities.Default.Settings;
import com.graysenko.NewPathBackEnd.Entities.User.User;
import com.graysenko.NewPathBackEnd.Repositories.Item.CartItemRepository;
import com.graysenko.NewPathBackEnd.Services.Default.SettingsService;
import com.graysenko.NewPathBackEnd.Services.User.UserService;
import com.graysenko.NewPathBackEnd.exceptions.AppError;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StripeController {

    @Value("${stripe.secret-key}")
    private String secret;
    private final UserService userService;
    private final SettingsService settingsService;
    private final CartItemRepository cartItemRepository;

    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) Map<String, String> body
            ) throws StripeException {
        Settings settings = settingsService.getSettings();
        if (!settings.isCollectionVisible()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AppError(403, "Collection is not available yet"));
        }

        String promocode = body != null ? body.get("promocode") : null;

        final int discountPercent;
        if (promocode != null) {
            Promocode promo = settingsService.checkPromocode(promocode);
            if (promo != null && promo.isActive()) {
                 discountPercent = promo.getDiscountPercent();
            }
            else {
                discountPercent = 0;
            }
        } else discountPercent = 0;

        Stripe.apiKey = secret;

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Token is empty"));

        String token = authHeader.substring(7);

        User user = userService.getUserFromToken(token);
        if (user == null) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid token"));

        List<CartItem> cartItems = cartItemRepository.findAllByUserEmail(user.getEmail());

        if (cartItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Token is not valid"));
        }

        List<SessionCreateParams.LineItem> sessionCreateParams = cartItems.stream()
                .map(cartItem -> SessionCreateParams.LineItem.builder()
                        .setQuantity((long) cartItem.getQuantity())
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount((long) (cartItem.getItem().getPrice() * 100 * (1 - discountPercent / 100.0)))
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(cartItem.getItem().getName())
                                        .addImage(cartItem.getItem().getFrontImage())
                                        .build()).build()).build()
                ).toList();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/success")
                .setCancelUrl("http://localhost:5173/cancel")
                .putMetadata("promocode", promocode != null ? promocode : "")
                .addAllLineItem(sessionCreateParams).build();

        Session session = Session.create(params);
        return ResponseEntity.ok(Map.of("url", session.getUrl()));
    }
}
