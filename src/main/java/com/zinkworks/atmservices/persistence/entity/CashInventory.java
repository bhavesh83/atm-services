package com.zinkworks.atmservices.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CASH_INVENTORY")
public class CashInventory {
    @Id
    int denomination;
    int numberOfNotes;
}