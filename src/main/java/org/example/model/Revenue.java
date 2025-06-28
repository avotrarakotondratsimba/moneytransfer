package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "revenue")
public class Revenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "revenue_id")
    private Long revenueId;

    @Column(name = "month", nullable = false)
    private int month;

    @Column(name = "year", length = 4, nullable = false)
    private int year;

    @Column(name = "total_fee", nullable = false) // male or female
    private int totalFee;

    // --- Constructors ---

    public Revenue() {
    }

    public Revenue(int month, int year, int totalFee) {
        this.month = month;
        this.year = year;
        this.totalFee = totalFee;
    }

    // --- Getters and Setters ---

    public Long getRevenueId() {
        return revenueId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

}
