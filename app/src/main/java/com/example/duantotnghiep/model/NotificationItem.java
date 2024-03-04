package com.example.duantotnghiep.model;

public class NotificationItem implements Comparable<NotificationItem> {
    private String content;
    private String dateTime;
    private String title;
    private String userID;
    private boolean isRead;

    public NotificationItem() {

    }

    public NotificationItem(String content, String dateTime, String title) {
        this.content = content;
        this.dateTime = dateTime;
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public int compareTo(NotificationItem other) {
        if (this.getDateTime() == null && other.getDateTime() == null) {
            return 0;
        } else if (this.getDateTime() == null) {
            return 1;
        } else if (other.getDateTime() == null) {
            return -1;
        } else {
            return other.getDateTime().compareTo(this.getDateTime());
        }
    }
}

