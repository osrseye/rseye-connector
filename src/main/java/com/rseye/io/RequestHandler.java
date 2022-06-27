package com.rseye.io;

import com.rseye.ConnectorConfig;
import okhttp3.*;
import okhttp3.internal.annotations.EverythingIsNonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class RequestHandler {
    public static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final ConnectorConfig config;

    public RequestHandler(ConnectorConfig config) {
        this.client = new OkHttpClient();
        this.config = config;
    }

    public void execute(Endpoint endpoint, String data) {
        Request request = new Request.Builder()
                .url(config.baseEndpoint() + endpoint.location)
                .header("Authorization", "Bearer: " + config.bearerToken())
                .header("X-Request-Id", UUID.randomUUID().toString())
                .post(RequestBody.create(JSON, data))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call call, Response response) {
                log.debug("Call response: Endpoint: {}, Contents: {}", endpoint.ordinal(), response.body().toString());
                response.close();
            }
            @Override
            @EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                log.debug("Issue detected while posting to endpoint: {}", e.getMessage());
            }
        });
    }

    public enum Endpoint {
        POSITION_UPDATE("position_update/"),
        LOGIN_UPDATE("login_update/"),
        STAT_UPDATE("stat_update/"),
        QUEST_UPDATE("quest_update/"),
        BANK_UPDATE("bank_update/"),
        LOOT_UPDATE("loot_update/"),
        INVENTORY_UPDATE("inventory_update/"),
        EQUIPMENT_UPDATE("equipment_update/");

        public final String location;
        Endpoint(String location) {
            this.location = location;
        }
    }
}
