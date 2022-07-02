package com.rseye.util;

import com.google.gson.Gson;
import com.rseye.io.RequestHandler;

public interface Postable {
    Gson gson = new Gson();
    default String toJson() {
        return gson.toJson(this);
    }
    RequestHandler.Endpoint endpoint();
}
