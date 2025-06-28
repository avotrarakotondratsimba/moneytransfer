package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "monetary_unit")
public class MonetaryUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Long unitId;

    @Column(length = 3, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    // --- Constructors ---

    public MonetaryUnit() {}

    public MonetaryUnit(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // --- Getters and Setters ---

    public Long getUnitId() { return unitId; }

    public String getCode() { return code; }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
