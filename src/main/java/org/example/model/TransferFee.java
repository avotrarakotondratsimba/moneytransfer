package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "transfer_fee")
public class TransferFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long transferId;

    @Column(name = "min_amount", nullable = false)
    private Integer minAmount;

    @Column(name = "max_amount", nullable = false)
    private Integer maxAmount;

    @Column(name = "fee_amount", nullable = false)
    private Integer feeAmount;

    @ManyToOne
    @JoinColumn(nullable = false)
    private MonetaryUnit currency;

    // --- Constructors ---

    public TransferFee() {}

    public TransferFee(Integer minAmount, Integer maxAmount, Integer feeAmount, MonetaryUnit currency) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.feeAmount = feeAmount;
        this.currency = currency;
    }

    // --- Getters and Setters ---

    public Long getTransferId() { return transferId; }

    public Integer getMinAmount() { return minAmount; }

    public void setMinAmount(Integer minAmount) { this.minAmount = minAmount; }

    public Integer getMaxAmount() { return maxAmount; }

    public void setMaxAmount(Integer maxAmount) { this.maxAmount = maxAmount; }

    public Integer getFeeAmount() { return feeAmount; }

    public void setFeeAmount(Integer feeAmount) { this.feeAmount = feeAmount; }

    public MonetaryUnit getCurrency() { return currency; }

    public void setCurrency(MonetaryUnit currency) { this.currency = currency; }
}
