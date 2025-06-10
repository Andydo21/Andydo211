package com.andd.DoDangAn.DoDangAn.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "episode")
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "episode_id")
    private Long id;

    @NotNull
    @Size(min = 1, max = 200, message = "Tiêu đề tập phải từ 1 đến 200 ký tự")
    private String title;

    @ManyToOne
    @JoinColumn(name = "productID", nullable = false)
    private Product product;

    private String episode;

    @Column(name = "videoUrl")
    private String videoPublicId;

    @NotNull(message = "Hình ảnh không được để trống")
    private String imageUrl = "/uploads/default.png";

    private String duration;

    private String episodeTitle;

    // Constructors
    public Episode() {}

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getEpisode() { return episode; }
    public void setEpisode(String episode) { this.episode = episode; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getEpisodeTitle() { return episodeTitle; }
    public void setEpisodeTitle(String episodeTitle) { this.episodeTitle = episodeTitle; }
    public String getVideoPublicId() { return videoPublicId; }
    public void setVideoPublicId(String videoPublicId) { this.videoPublicId = videoPublicId; }
}