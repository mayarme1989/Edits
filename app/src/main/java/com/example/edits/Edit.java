package com.example.edits;

import java.io.Serializable;

public class Edit implements Serializable {
    private String id;
    private String title;
    private String showName;
    private String characterName;
    private String embedUrl;
    private String imageUrl;

    public Edit() {
        // Required empty constructor for Firestore
    }

    public Edit(String id, String title, String showName, String characterName, String embedUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.showName = showName;
        this.characterName = characterName;
        this.embedUrl = embedUrl;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }

    public void setEmbedUrl(String embedUrl) {
        this.embedUrl = embedUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}