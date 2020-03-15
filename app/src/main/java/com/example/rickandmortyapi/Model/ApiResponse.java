package com.example.rickandmortyapi.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ApiResponse implements Serializable {

    @SerializedName("info")
    private Info info;
    @SerializedName("results")
    private List<Characters> results;

    public ApiResponse(Info info, List<Characters> characters) {
        this.info = info;
        this.results = characters;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<Characters> getCharacters() {
        return results;
    }

    public void setCharacters(List<Characters> characters) {
        this.results = characters;
    }
}
