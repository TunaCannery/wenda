package com.nowcoder.model;

import java.util.Date;

public class Question {
    private int id;
    private String title;
    private String content;
    private String contentDetail;
    private Date createdDate;
    private int userId;
    private int commentCount;

    public String getContent_detail() {
        return contentDetail;
    }

    public void setContent_detail(String content_detail) {
        this.contentDetail = content_detail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
