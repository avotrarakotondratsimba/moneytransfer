package org.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rate")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_id")
    private Long exchangeId;

    @Column(nullable = false)
    private Integer baseAmount;

    @Column(name = "rate_to_MGA", nullable = false)
    private Integer rateToMGA;

    @Column(name = "modification_date", nullable = false)
    private LocalDateTime modificationDate;

    @ManyToOne
    @JoinColumn(name = "source_currency", nullable = false, unique = true)
    private MonetaryUnit sourceCurrency;

    // --- Constructors ---

    public ExchangeRate(){}

    public ExchangeRate(Integer baseAmount, Integer rateToMGA, MonetaryUnit sourceCurrency, LocalDateTime modificationDate) {
        this.baseAmount = baseAmount;
        this.rateToMGA = rateToMGA;
        this.sourceCurrency = sourceCurrency;
        this.modificationDate = modificationDate;
    }

    // --- Getters and Setters ---

    public Long getExchangeId() { return exchangeId; }

    public Integer getBaseAmount() { return baseAmount; }

    public void setBaseAmount(Integer baseAmount) { this.baseAmount = baseAmount; }

    public Integer getRateToMGA() { return rateToMGA; }

    public void setRateToMGA(Integer rateToMGA) { this.rateToMGA = rateToMGA; }

    public LocalDateTime getModificationDate() { return modificationDate; }

    public void setModificationDate(LocalDateTime modificationDate) { this.modificationDate = modificationDate; }

    public MonetaryUnit getSourceCurrency() { return sourceCurrency; }

    public void setSourceCurrency(MonetaryUnit sourceCurrency) { this.sourceCurrency = sourceCurrency; }
}
