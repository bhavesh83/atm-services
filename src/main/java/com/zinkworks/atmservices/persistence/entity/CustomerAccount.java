package com.zinkworks.atmservices.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CUSTOMER_ACCOUNT")
public class CustomerAccount {
    @Id
    String accountNumber;
    String hashedPin;
    int balance;
    int overDraftLimit;
}
