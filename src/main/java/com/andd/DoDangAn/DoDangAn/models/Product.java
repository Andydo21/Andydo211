package com.andd.DoDangAn.DoDangAn.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Calendar;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name="Products")
public class Product {
    @Id
    @Column(name="productID", columnDefinition = "VARCHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String id;
    
    @Column(columnDefinition = "VARCHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String categoryID;

    @Column(name="productName", columnDefinition = "VARCHAR(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    @NotNull
    @NotBlank(message = "product name cannot be null")
    @Size(min=3,max=300,message = "thieu roi con ga")
    private String productName;
    private int season;
    @Min(0)
    @Column(name="price")
    private int viewCount;
    private Double score;
    private Integer rate;
    @NotNull
    @Size(min=5,max=1000)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable=false)
    private Category category;

    @NotNull(message = "Ảnh không được để trống")
    private String imageUrl ;
    @JoinColumn(name="episodeType")
    private String episode;
    private String likes;
    @Column(name="videoUrl")
    private String videoPublicId;
    private LocalDateTime releaseDate;
    private String released;
    private String duration;
    private String Actor;
    private String Director;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "countryID", nullable = false)
    private Country country;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMovieList> userMovieLists = new ArrayList<>();

    public Product() {
        this.rate = 0;
        this.releaseDate = LocalDateTime.now();
        this.score = 0.0;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public Product(String id,Category category, String categoryID, String productName, int viewCount, String description, String imageUrl, String videoUrl, String episode, LocalDateTime releaseDate, String released, Double score, String duration, Integer season,Country country) {
        this.id = id;
        this.categoryID = categoryID;
        this.category = category;
        this.viewCount = viewCount;
        this.productName = productName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.releaseDate = LocalDateTime.now();
        this.episode = episode;
        this.released = released;
        this.score=score;
        this.duration = duration;
        this.season = season;
        this.country = country;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public String getVideoPublicId() { return videoPublicId; }

    public void setVideoPublicId(String videoPublicId) { this.videoPublicId = videoPublicId; }


    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
        if (categoryID != null) {
            this.category = new Category();
            this.category.setCategoryID(categoryID);
        } else {
            this.category = null;
        }
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getViewCount(){
        return viewCount;
    }

    public void setViewCount(int price) {
        this.viewCount = viewCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public @NotNull Double getScore() {
        return score;
    }

    public void setScore(@NotNull Double score) {
        this.score = score;
    }
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public @NotNull Integer getRate() {
        return rate;
    }

    public void setRate(@NotNull Integer rate) {
        this.rate = rate;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public @NotNull String getDuration() {
        return duration;
    }

    public void setDuration(@NotNull String duration) {
        this.duration = duration;
    }

    public String getActor() {
        return Actor;
    }

    public void setActor(String actor) {
        Actor = actor;
    }

    public String getDirector() {
        return Director;
    }

    public void setDirector(String director) {
        Director = director;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<UserMovieList> getUserMovieLists() {
        return userMovieLists;
    }

    public void setUserMovieLists(List<UserMovieList> userMovieLists) {
        this.userMovieLists = userMovieLists;
    }
}

