package com.rseye.update;

import com.rseye.io.RequestHandler;
import com.rseye.util.Postable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.SkullIcon;

public class SkullUpdate implements Postable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private SkullIcon skull;

    public SkullUpdate(String username, SkullIcon skull) {
        this.username = username;
        this.skull = skull;
    }

    @Override
    public RequestHandler.Endpoint endpoint() {
        return RequestHandler.Endpoint.SKULL_UPDATE;
    }
}
