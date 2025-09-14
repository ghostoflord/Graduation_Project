package com.vn.capstone.domain.response.notification;

public class NotificationUpdateDTO {
    private String title;
    private String content;
    private Boolean isRead;
    private Boolean forAll;

    // getters và setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getForAll() {
        return forAll;
    }

    public void setForAll(Boolean forAll) {
        this.forAll = forAll;
    }
}
