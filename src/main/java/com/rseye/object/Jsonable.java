package com.rseye.object;

import com.google.gson.Gson;

public class Jsonable {
    private static final Gson gson = new Gson();

    public String toJson() {
        return gson.toJson(this);
    }
}
