package com.zinkworks.atmservices.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int transactionDetailsId;
    int denomination;
    int numberOfNotes;
    @JoinColumn(name = "TRANSACTION_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    Transactions transactions;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof TransactionsDetails)) return false;
        TransactionsDetails transactionsDetails = (TransactionsDetails) o;
        return getDenomination() == transactionsDetails.getDenomination()
                && getNumberOfNotes() == transactionsDetails.getNumberOfNotes()
                && getTransactions().getTransactionId() == transactionsDetails.getTransactions().getTransactionId();
    }

    @Override
    public String toString() {
        return getDenomination() + " " + getNumberOfNotes() + " " + getTransactions().getTransactionId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(denomination, numberOfNotes, transactions.getTransactionId());
    }
}