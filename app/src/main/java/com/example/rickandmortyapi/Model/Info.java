package com.example.rickandmortyapi.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Info implements Serializable {
    @SerializedName("count")
    private Integer count;
    @SerializedName("pages")
    private Integer pages;
    @SerializedName("next")
    private String next;
    @SerializedName("prev")
    private String prev;

    public Info(Integer count, Integer pages, String next, String previous) {
        this.count = count;
        this.pages = pages;
        this.next = next;
        this.prev = previous;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return prev;
    }

    public void setPrevious(String previous) {
        this.prev = previous;
    }
}
