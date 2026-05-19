package com.graysenko.NewPathBackEnd.API.Item;

import com.graysenko.NewPathBackEnd.DTOs.Item.ItemDTO;
import com.graysenko.NewPathBackEnd.DTOs.Item.ProductVariantsDTO;
import com.graysenko.NewPathBackEnd.Entities.Default.Settings;
import com.graysenko.NewPathBackEnd.Entities.Item.Item;
import com.graysenko.NewPathBackEnd.Services.Default.SettingsService;
import com.graysenko.NewPathBackEnd.Services.Item.ItemService;
import com.graysenko.NewPathBackEnd.exceptions.AppError;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/api/items")
public class ItemsController {

    private final ItemService itemService;
    private final SettingsService settingsService;

    @Autowired
    public ItemsController(ItemService itemService, SettingsService settingsService) {
        this.itemService = itemService;
        this.settingsService = settingsService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getItems() {
        Settings settings = settingsService.getSettings();
        if (!settings.isCollectionVisible()) {
            return ResponseEntity.ok(List.of());
        }
        List<ItemDTO> itemDTOs = itemService.findAllByDeletedFalse().stream().map((
                item -> new ItemDTO(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getFrontImage(),
                        item.getBackImage(),
                        itemService.isInStock(item),
                        item.isDeleted(),
                        null, null
                ))).toList();

        return ResponseEntity.ok(itemDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Settings settings = settingsService.getSettings();
        if (!settings.isCollectionVisible()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AppError(403, "Collection is not available yet"));
        }

        Optional<Item> itemOpt = itemService.findById(id);

        if (!itemOpt.isPresent()) return ResponseEntity.notFound().build();
        Item item = itemOpt.get();

        if (item.isDeleted()) return ResponseEntity.notFound().build();

        List<ProductVariantsDTO> pvDto = item.getProductVariant().stream().map(
                variant -> new ProductVariantsDTO(
                        variant.getSize(),
                        variant.getColor(),
                        variant.getQuantity()
                )).toList();

        ItemDTO itemDTO = new ItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getFrontImage(),
                item.getBackImage(),
                itemService.isInStock(item),
                false,
                item.getImages(),
                pvDto
        );
        return ResponseEntity.ok(itemDTO);
    }
}
