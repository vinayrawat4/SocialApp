package com.vinay.socialapp.Model;

import java.util.ArrayList;

public class Storymodel {

  String storyBy;

  public Storymodel() {
  }

  public String getStoryBy() {
    return storyBy;
  }

  public void setStoryBy(String storyBy) {
    this.storyBy = storyBy;
  }

  public long getStoryAt() {
    return StoryAt;
  }

  public void setStoryAt(long storyAt) {
    StoryAt = storyAt;
  }


  long StoryAt;

  public ArrayList<UserStory> getStoriesList() {
    return storiesList;
  }

  public void setStoriesList(ArrayList<UserStory> storiesList) {
    this.storiesList = storiesList;
  }

  ArrayList<UserStory> storiesList;


}
