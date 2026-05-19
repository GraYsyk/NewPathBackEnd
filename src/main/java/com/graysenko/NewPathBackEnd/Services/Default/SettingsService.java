package com.graysenko.NewPathBackEnd.Services.Default;

import com.graysenko.NewPathBackEnd.Entities.Default.Promocode;
import com.graysenko.NewPathBackEnd.Entities.Default.Settings;
import com.graysenko.NewPathBackEnd.Entities.User.User;
import com.graysenko.NewPathBackEnd.Repositories.Default.PromocodeRepository;
import com.graysenko.NewPathBackEnd.Repositories.Default.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SettingsService {
    private final SettingsRepository settingsRepository;
    private final PromocodeRepository promocodeRepository;

    public Settings getSettings() {
        return settingsRepository.findById(1L).get();
    }

    public Promocode checkPromocode(String code) {
        Promocode promo = promocodeRepository.findByCode(code).orElse(null);
        if (promo == null) return null;
        if (!promo.isActive())  return null;
        if (promo.getExpiresAt() != null && promo.getExpiresAt().getTime() < System.currentTimeMillis()) return null;
        if (promo.getUsageLimit() != null && promo.getUsedCount() >= promo.getUsageLimit()) {
            promo.setActive(false);
            return null;
        }
        return promo;
    }

    public void incrementPromocode(String code) {
        Promocode promo = checkPromocode(code);
        if (promo == null) return;
        promo.setUsedCount(promo.getUsedCount() + 1);
        promocodeRepository.save(promo);
    }

    public void saveSettings(Settings settings) {
        settingsRepository.save(settings);
    }

    public List<Promocode> getPromocodes() {
        return promocodeRepository.findAll();
    }

    public Promocode findPromoById(Long id) {
        return promocodeRepository.findById(id).orElse(null);
    }
    public Promocode findPromocodeByCode(String code) {
        return promocodeRepository.findByCode(code).orElse(null);
    }

    public void savePromocode(Promocode promocode) {
        promocodeRepository.save(promocode);
    }

    public void deletePromocode(Promocode promocode) {
        promocodeRepository.delete(promocode);
    }

    @Scheduled(fixedRate = 60000)
    public void scheduleDropAt () {
        Settings settings = getSettings();
        if (settings == null) return;

        if (settings.getScheduleDropAt() != null &&
                settings.getScheduleDropAt().getTime() < System.currentTimeMillis()) {
            settings.setCollectionVisible(true);
            settings.setScheduleDropAt(null);
            settingsRepository.save(settings);
        }
    }
}
