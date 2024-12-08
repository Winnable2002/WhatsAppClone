package com.example.baicuoikywhatsappclone.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageModel {

    private String uID;           // ID người gửi
    private String message;       // Nội dung tin nhắn
    private String messageId;     // ID của tin nhắn trong Firebase
    private long timestamp; // Thời gian gửi tin nhắn (kiểu long)

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    private String senderName;


    // Constructor đầy đủ (có timestamp)
    public MessageModel(String uID, String message, long timestamp) {
        this.uID = uID;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Constructor rút gọn (không có timestamp, mặc định là thời gian hiện tại)
    public MessageModel(String uID, String message) {
        this.uID = uID;
        this.message = message;
        this.timestamp = System.currentTimeMillis(); // Lấy thời gian hiện tại
    }

    // Constructor mặc định (dành cho Firebase)
    public MessageModel() {}

    // Getter và Setter cho uID
    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    // Getter và Setter cho message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter và Setter cho messageId
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    // Getter và Setter cho timestamp
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Phương thức tiện ích: Chuyển timestamp thành định dạng thời gian/ngày
    public String getFormattedTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }
}
