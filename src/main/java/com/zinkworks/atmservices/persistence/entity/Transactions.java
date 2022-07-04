package com.zinkworks.atmservices.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int transactionId;
    @OneToMany(mappedBy = "transactions", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<TransactionsDetails> transactionsDetails;
    int amount;
    String accountNumber;
    TransactionType transactionType;
}
