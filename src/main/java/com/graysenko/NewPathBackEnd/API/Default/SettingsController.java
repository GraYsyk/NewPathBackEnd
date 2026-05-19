package com.graysenko.NewPathBackEnd.API.Default;

import com.graysenko.NewPathBackEnd.DTOs.Default.PromoDTO;
import com.graysenko.NewPathBackEnd.DTOs.Default.SettingsDTO;
import com.graysenko.NewPathBackEnd.Entities.Default.Promocode;
import com.graysenko.NewPathBackEnd.Entities.Default.Settings;
import com.graysenko.NewPathBackEnd.Services.Default.SettingsService;
import com.graysenko.NewPathBackEnd.exceptions.AppError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/settings")
public class SettingsController {
    private final SettingsService settingsService;

    @GetMapping
    public ResponseEntity<?> getSettings() {
        Settings settings = settingsService.getSettings();
        if (!settings.isCollectionVisible()) {
            return ResponseEntity.status(HttpStatus.OK).body(new SettingsDTO(
                    "NEW DROP SOON", "Our new collection is on its way—stay tuned!"
            ));
        }
        return ResponseEntity.ok(settings);
    }

    @GetMapping("/check/{promo}")
    public ResponseEntity<?> checkPromo(@PathVariable String promo) {
        Promocode promocode = settingsService.checkPromocode(promo);
        if (promocode != null && promocode.isActive()) {
            return ResponseEntity.ok(Map.of("discount", promocode.getDiscountPercent()));
        }
        return ResponseEntity.badRequest().body("Invalid or expired code.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/promo")
    public ResponseEntity<?> getPromocodes() {
        List<Promocode> promos = settingsService.getPromocodes();
        return ResponseEntity.ok(promos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/promo")
    public ResponseEntity<?> createPromocode(@RequestBody PromoDTO promoDTO) {
        if (promoDTO == null) return ResponseEntity.badRequest().body("Invalid data.");
        Promocode promo = new Promocode();
        promo.setCode(promoDTO.getCode().toUpperCase());
        promo.setDiscountPercent(promoDTO.getDiscountPercent());
        promo.setUsageLimit(promoDTO.getUsageLimit());
        promo.setActive(true);
        promo.setExpiresAt(new Timestamp(promoDTO.getExpiresAt()));
        settingsService.savePromocode(promo);
        return ResponseEntity.ok(promo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/promo") //ONLY STATUS CHANGE ALLOWED
    public ResponseEntity<?> updatePromocode(@RequestBody PromoDTO promocode) {
        if (promocode == null) return ResponseEntity.badRequest().body("Invalid data.");
        Promocode promo = settingsService.findPromocodeByCode(promocode.getCode());
        promo.setActive(promocode.isActive());
        settingsService.savePromocode(promo);
        return ResponseEntity.ok(promo);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/promo/{id}")
    public ResponseEntity<?> deletePromocode(@PathVariable Long id) {
        Promocode promo = settingsService.findPromoById(id);
        if (promo == null) return ResponseEntity.badRequest().body("Invalid promo id.");
        settingsService.deletePromocode(promo);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/drop")
    public ResponseEntity<?> drop(@RequestParam Long dropDate) {
        Settings settings = settingsService.getSettings();
        if (dropDate == null) return ResponseEntity.badRequest().body("Invalid date.");
        settings.setScheduleDropAt(new Timestamp(dropDate));
        settingsService.saveSettings(settings);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/visibility")
    public ResponseEntity<?> changeVisibility(@RequestParam boolean visibilityStatus) {
        Settings settings = settingsService.getSettings();
        settings.setCollectionVisible(visibilityStatus);
        settingsService.saveSettings(settings);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/label")
    public ResponseEntity<?> changeCollectionInfo(@RequestBody SettingsDTO settingsDTO) {
        Settings settings = settingsService.getSettings();
        settings.setCollectionName(settingsDTO.getCollectionName());
        settings.setCollectionDesc(settingsDTO.getCollectionDesc());
        settingsService.saveSettings(settings);
        return ResponseEntity.ok().build();
    }
}
