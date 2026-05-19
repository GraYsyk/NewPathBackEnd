package com.graysenko.NewPathBackEnd.API.Stripe;

import com.graysenko.NewPathBackEnd.Entities.CartItem;
import com.graysenko.NewPathBackEnd.Entities.Default.Promocode;
import com.graysenko.NewPathBackEnd.Entities.User.User;
import com.graysenko.NewPathBackEnd.Repositories.Item.CartItemRepository;
import com.graysenko.NewPathBackEnd.Services.Default.SettingsService;
import com.graysenko.NewPathBackEnd.Services.Item.ItemService;
import com.graysenko.NewPathBackEnd.Services.Order.OrderService;
import com.graysenko.NewPathBackEnd.Services.User.UserService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final OrderService orderService;
    private final SettingsService settingsService;
    private final CartItemRepository cartItemRepository;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody byte[] payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(
                    new String(payload, StandardCharsets.UTF_8),
                    sigHeader,
                    webhookSecret
            );
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            String json = event.getDataObjectDeserializer().getRawJson();
            Session session = Session.GSON.fromJson(json, Session.class);

            String customerEmail = session.getCustomerDetails().getEmail();
            String stripeSessionId = session.getId();
            String promocode = session.getMetadata().get("promocode");
            System.out.println(promocode);

            List<CartItem> cartItems = cartItemRepository.findAllByUserEmail(customerEmail);
            orderService.createOrder(cartItems, stripeSessionId, promocode);
            cartItemRepository.deleteAll(cartItems);

            if (promocode != null && !promocode.isEmpty()) {
                Promocode promo = settingsService.findPromocodeByCode(promocode);
                if (promo != null) {
                    if (!promo.getUsageLimit().equals(promo.getUsedCount())) {
                        settingsService.incrementPromocode(promocode);
                    }
                    if (promo.getUsageLimit().equals(promo.getUsedCount())) {
                        promo.setActive(false);
                        settingsService.savePromocode(promo);
                    }
                }
            }
        }

        return ResponseEntity.ok("success");
    }
}
