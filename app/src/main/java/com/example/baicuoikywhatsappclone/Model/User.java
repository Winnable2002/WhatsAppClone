package com.example.baicuoikywhatsappclone.Model;

public class User {

    String profilePic, Username, mail, password, UserID, lastMessage, status;

    public User()
    {

    }

    public User(String profilePic, String username, String mail, String password, String userID, String lastMessage, String status) {
        this.profilePic = profilePic;
        Username = username;
        this.mail = mail;
        this.password = password;
        UserID = userID;
        this.lastMessage = lastMessage;
        this.status = status;
    }

    public User(String username, String mail, String password) {
        Username = username;
        this.mail = mail;
        this.password = password;
    }


    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
