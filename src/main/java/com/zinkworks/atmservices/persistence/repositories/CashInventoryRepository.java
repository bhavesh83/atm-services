package com.zinkworks.atmservices.persistence.repositories;

import com.zinkworks.atmservices.persistence.entity.CashInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashInventoryRepository extends JpaRepository<CashInventory,Integer> {
}
