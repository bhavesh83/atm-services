package com.zinkworks.atmservices.persistence.repositories;

import com.zinkworks.atmservices.persistence.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionsRepository extends JpaRepository<Transactions,Integer> {
}
