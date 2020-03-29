package com.sliit.sliitmadnew02;

public class FindFriends
{

    public String profileimage1 , fullname1 , status1;

    public FindFriends(){}

    public FindFriends(String profileimage1, String fullname1, String status1)
    {
        this.profileimage1 = profileimage1;
        this.fullname1 = fullname1;
        this.status1 = status1;
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

    public String getStatus() {
        return status1;
    }

    public void setStatus(String status1) {
        this.status1 = status1;
    }

}
