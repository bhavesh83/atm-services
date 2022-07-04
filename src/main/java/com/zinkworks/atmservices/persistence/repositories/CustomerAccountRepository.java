package com.zinkworks.atmservices.persistence.repositories;

import com.zinkworks.atmservices.persistence.entity.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount,String> {
}
