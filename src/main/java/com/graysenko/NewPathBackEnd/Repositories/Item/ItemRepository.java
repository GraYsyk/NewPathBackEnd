package com.graysenko.NewPathBackEnd.Repositories.Item;

import com.graysenko.NewPathBackEnd.Entities.Item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByDeletedFalse();
}
