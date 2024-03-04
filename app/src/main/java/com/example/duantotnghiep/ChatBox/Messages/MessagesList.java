package com.example.duantotnghiep.ChatBox.Messages;

public class MessagesList {
    private String id, username,lassMessage,img;
    private int unseenMessages;

    public MessagesList(String id,String name,String lassMessage,String img,int unseenMessages) {
        this.id=id;
        this.username = name;
        this.lassMessage = lassMessage;
        this.img=img;
        this.unseenMessages = unseenMessages;
    }

    public MessagesList() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getLassMessage() {
        return lassMessage;
    }

    public void setLassMessage(String lassMessage) {
        this.lassMessage = lassMessage;
    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public void setUnseenMessages(int unseenMessages) {
        this.unseenMessages = unseenMessages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
