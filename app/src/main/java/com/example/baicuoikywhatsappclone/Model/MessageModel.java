package com.example.baicuoikywhatsappclone.Model;

public class MessageModel {

    String uID, message, messageId;
    Long timestam;



    public MessageModel(String uID, String message, Long timestam) {
        this.uID = uID;
        this.message = message;
        this.timestam = timestam;
    }


    public MessageModel(String uID, String message) {
        this.uID = uID;
        this.message = message;
    }


    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimestam() {
        return timestam;
    }

    public void setTimestam(Long timestam) {
        this.timestam = timestam;
    }

    public MessageModel(){

    }



}
