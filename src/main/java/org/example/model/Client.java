package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "client_name", length = 50, nullable = false)
    private String clientName;

    @Column(length = 6, nullable = false) // male or female
    private String gender;

    @ManyToOne
    @JoinColumn(referencedColumnName = "name", nullable = false)
    private Country country;

    @Column(nullable = false)
    private Integer balance;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // --- Constructors ---

    public Client() {
    }

    public Client(String phoneNumber, String clientName, String gender, Country country, Integer balance, String email, Boolean isActive) {
        this.phoneNumber = phoneNumber;
        this.clientName = clientName;
        this.gender = gender;
        this.country = country;
        this.balance = balance;
        this.email = email;
        this.isActive = isActive;
    }

    // --- Getters and Setters ---

    public Long getClientId() {
        return clientId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
