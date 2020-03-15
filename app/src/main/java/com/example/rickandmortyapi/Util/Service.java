package com.example.rickandmortyapi.Util;

import com.example.rickandmortyapi.Model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {
    @GET("character/")
    Call<ApiResponse> getCharacters(@Query("page") int pageIndex);
}
