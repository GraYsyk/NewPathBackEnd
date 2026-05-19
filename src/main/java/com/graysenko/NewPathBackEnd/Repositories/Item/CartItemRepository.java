package com.graysenko.NewPathBackEnd.Repositories.Item;

import com.graysenko.NewPathBackEnd.Entities.CartItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem, Long> {
    List<CartItem> findAllByUserEmail(String email);
}
