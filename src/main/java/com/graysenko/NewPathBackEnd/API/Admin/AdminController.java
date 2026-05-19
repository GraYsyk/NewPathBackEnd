package com.graysenko.NewPathBackEnd.API.Admin;

import com.graysenko.NewPathBackEnd.DTOs.Item.ItemDTO;
import com.graysenko.NewPathBackEnd.DTOs.Item.ProductVariantsDTO;
import com.graysenko.NewPathBackEnd.DTOs.Stats.BestsellerDTO;
import com.graysenko.NewPathBackEnd.Entities.Item.Item;
import com.graysenko.NewPathBackEnd.Entities.Item.ProductVariant;
import com.graysenko.NewPathBackEnd.Services.Admin.AdminService;
import com.graysenko.NewPathBackEnd.Services.Default.CloudinaryService;
import com.graysenko.NewPathBackEnd.Services.Item.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final CloudinaryService cloudinaryService;
    private final ItemService itemService;

    //STATS

    @GetMapping("/ordersStatus")
    public ResponseEntity<?> getOrdersStatus() {
        return ResponseEntity.ok(adminService.getOrderStatus());
    }

    @GetMapping("/bestseller")
    public ResponseEntity<?> getBestseller() {
        BestsellerDTO bestseller = adminService.getBestseller();
        if (bestseller == null) return ResponseEntity.ok(null);
        return ResponseEntity.ok(bestseller);
    }

    @GetMapping("/monthlyGrowth")
    public ResponseEntity<?> getMonthlyGrowth() {
        return ResponseEntity.ok(adminService.getMonthlyGrowth());
    }

    @GetMapping("/income")
    public ResponseEntity<?> getIncome(@RequestParam String period) {
        return ResponseEntity.ok(adminService.getIncome(period));
    }

    //ITEMS
    @GetMapping("/items")
    public ResponseEntity<?> getAdminItems() {
        List<ItemDTO> itemDTOs = itemService.findAll().stream().map((
                item -> new ItemDTO(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getFrontImage(),
                        item.getBackImage(),
                        itemService.isInStock(item),
                        item.isDeleted(),
                        item.getImages(),
                        item.getProductVariant() == null ? List.of() :
                                item.getProductVariant().stream()
                                        .map(v ->
                                                new ProductVariantsDTO(
                                                        v.getSize(), v.getColor(), v.getQuantity())).toList()
                ))).toList();
        return ResponseEntity.ok(itemDTOs);
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(@RequestBody ItemDTO itemDTO) {
        if (itemDTO == null) return ResponseEntity.badRequest().build();
        Item item = new Item();
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setPrice(itemDTO.getPrice());
        item.setFrontImage(itemDTO.getFrontImage());
        item.setBackImage(itemDTO.getBackImage());

        List<String> images = new ArrayList<>();
        images.add(itemDTO.getFrontImage());
        images.add(itemDTO.getBackImage());
        images.addAll(itemDTO.getImages());

        item.setImages(images);
        item.setProductVariant(itemDTO.getVariants()
                .stream().map(v -> new ProductVariant(
                        v.getSize(), v.getColor(), v.getQuantity(), item))
                .toList());
        itemService.save(item);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id,
                                        @RequestBody ItemDTO itemDTO) {
        Item item = itemService.findById(id).orElse(null);
        if (item == null) return ResponseEntity.notFound().build();

        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setPrice(itemDTO.getPrice());
        item.setFrontImage(itemDTO.getFrontImage());
        item.setBackImage(itemDTO.getBackImage());
        item.setImages(itemDTO.getImages());

        item.getProductVariant().clear();
        item.getProductVariant().addAll(
                itemDTO.getVariants().stream()
                        .map(v -> new ProductVariant(v.getSize(), v.getColor(), v.getQuantity(), item))
                        .toList()
        );
        itemService.save(item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        Item item = itemService.findById(id).orElse(null);
        if (item == null) return ResponseEntity.notFound().build();
        itemService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/items/{id}/restore")
    public ResponseEntity<?> restoreItem(@PathVariable Long id) {
        Item item = itemService.findById(id).orElse(null);
        if (item == null) return ResponseEntity.notFound().build();
        item.setDeleted(false);
        itemService.save(item);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String url = cloudinaryService.upload(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
