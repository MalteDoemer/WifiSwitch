package com.example.wifiswitch;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;


public class HttpGetResult {
    private final int code;

    @Nullable
    private final JsonObject response;

    public HttpGetResult(int code, @Nullable JsonObject response) {
        this.code = code;
        this.response = response;
    }



    public int getCode() {
        return code;
    }

    @Nullable
    public JsonObject getResponse() {
        return response;
    }
}
