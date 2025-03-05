package com.vinay.socialapp.Model;

public class UserStory {
    String image;
    long storyAt;


    public UserStory(String image, long storyAt) {
        this.image = image;
        this.storyAt = storyAt;
    }

    public UserStory() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }
}