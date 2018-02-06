package com.newtally.core.dto;

public class MobileNotification {
    private String to;
    private Notification notification;
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public Notification getNotification() {
        return notification;
    }
    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
