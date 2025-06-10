package com.andd.DoDangAn.DoDangAn.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name="country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, length = 36)
    private String countryId;

    private String countryName;

    public Country() {
    }

    public Country(String countryId, String countryName) {
        this.countryId = UUID.randomUUID().toString();
        this.countryName = countryName;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
