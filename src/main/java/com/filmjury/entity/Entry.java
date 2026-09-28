package com.filmjury.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a short film entry submitted to the contest.
 * Each entry has a title, genre, and video link.
 * An entry can be scored by multiple judges through scorecards.
 */
@Entity
@Table(name = "entries")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Genre is required")
    @Column(nullable = false)
    private String genre;

    @NotBlank(message = "Video link is required")
    @Column(name = "video_link", nullable = false)
    private String videoLink;

    // Default constructor required by JPA
    public Entry() {
    }

    public Entry(String title, String genre, String videoLink) {
        this.title = title;
        this.genre = genre;
        this.videoLink = videoLink;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }
}
