package com.graysenko.NewPathBackEnd.Services.Item;

import com.graysenko.NewPathBackEnd.Entities.Item.Item;
import com.graysenko.NewPathBackEnd.Entities.Item.ProductVariant;
import com.graysenko.NewPathBackEnd.Repositories.Item.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public List<Item> findAllByDeletedFalse() {
        return itemRepository.findAllByDeletedFalse();
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Transactional
    public void sellVariants(ProductVariant productVariant, int quantity, Item item) {
        if (productVariant != null) {
            productVariant.setQuantity(productVariant.getQuantity() - quantity);
            if (productVariant.getQuantity() <= 0) {
                item.getProductVariant().remove(productVariant);
            }
            itemRepository.save(item);
        }
    }

    public ProductVariant findVariant(Item item, String size, String color) {
        return item.getProductVariant().stream()
                .filter(v -> v.getSize().equals(size) && v.getColor().equals(color))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Variant not found"));
    }

    @Transactional
    public void save(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) return;
        item.setDeleted(true);
        itemRepository.save(item);
    }

    public boolean isInStock(Item item){
        return !item.getProductVariant().isEmpty();
    }
}
