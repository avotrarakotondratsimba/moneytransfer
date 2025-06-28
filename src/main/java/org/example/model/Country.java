package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id")
    private Long countryId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "phone_code", nullable = false, length = 10)
    private String phoneCode;

    @ManyToOne
    @JoinColumn(name = "currency_code", referencedColumnName = "code", nullable = false)
    private MonetaryUnit currency;

    // --- Constructors ---
    public Country() {
    }

    public Country(String name, String phoneCode, MonetaryUnit currency) {
        this.name = name;
        this.phoneCode = phoneCode;
        this.currency = currency;
    }

    // --- Getters and Setters ---
    public Long getCountryId() {
        return countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public MonetaryUnit getCurrency() {
        return currency;
    }

    public void setCurrency(MonetaryUnit currency) {
        this.currency = currency;
    }
}
