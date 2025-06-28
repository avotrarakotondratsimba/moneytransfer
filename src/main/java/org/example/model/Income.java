package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "income")
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_id")
    private int incomeId;

    private int amount;

    @ManyToOne
    private MonetaryUnit currency;


    // --- Constructors ---

    public Income() {}

    public Income(int amount, MonetaryUnit currency) {
        this.amount = amount;
        this.currency = currency;
    }

    // --- Getters and Setters ---
    public int getAmount() { return amount; }

    public void setAmount(int amount) { this.amount = amount; }

    public MonetaryUnit getCurrency() { return currency; }

    public void setCurrency(MonetaryUnit sender) { this.currency = currency; }

}
