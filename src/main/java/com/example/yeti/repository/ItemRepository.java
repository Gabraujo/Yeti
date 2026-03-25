package com.example.yeti.repository;

import com.example.yeti.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findBySector_Code(String sectorCode);
}
