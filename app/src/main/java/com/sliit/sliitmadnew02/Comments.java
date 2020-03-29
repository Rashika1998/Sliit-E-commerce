package com.sliit.sliitmadnew02;

public class Comments
{

    public String comment , date , time , username1 , profileimage1 , fullname1 ;
    public Comments(){}

    public Comments(String comment, String date, String time, String username1 , String profileimage1 , String fullname1 )
    {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.username1 = username1;
        this.profileimage1 = profileimage1;
        this.fullname1 = fullname1;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getData() {
        return date;
    }

    public void setData(String data) {
        this.date = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username1;
    }

    public void setUsername(String username1) {
        this.username1 = username1;
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


}
