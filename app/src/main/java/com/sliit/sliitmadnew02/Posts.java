package com.sliit.sliitmadnew02;

public class Posts
{

    public String uid , time , data , postimage1 , name1 , description1 , link1 , profileimage1 , fullname1 , country1;

    public Posts(){}
    public Posts(String uid, String time, String data, String postimage1, String name1 , String description1 , String link1, String profileimage1, String fullname1 , String country1) {
        this.uid = uid;
        this.time = time;
        this.data = data;
        this.postimage1 = postimage1;
        this.name1 = name1;
        this.description1 = description1;
        this.link1 = link1;
        this.profileimage1 = profileimage1;
        this.fullname1 = fullname1;
        this.country1 = country1;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPostimage() {
        return postimage1;
    }

    public void setPostimage(String postimage1) {
        this.postimage1 = postimage1;
    }

    public String getItemName() {
        return name1;
    }

    public void setItemName(String name1) {
        this.name1 = name1;
    }

    public String getDescription() {
        return description1;
    }

    public void setDescription(String description1) {
        this.description1 = description1;
    }

    public String getItemLink() {
        return link1;
    }

    public void setItemLink(String link1) {
        this.link1 = link1;
    }


    public String getProfileimage() {
        return profileimage1;
    }

    public void setProfileimage(String profileimage1) {
        this.profileimage1 = profileimage1;
    }

    public String getFullname() {
        return fullname1;
    }

    public void setFullname(String fullname1) {
        this.fullname1 = fullname1;
    }

    public String getCountry() {
        return country1;
    }

    public void setCountry(String country1) {
        this.country1 = country1;
    }

}
