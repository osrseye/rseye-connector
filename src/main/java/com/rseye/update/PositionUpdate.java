package com.rseye.update;

import com.rseye.io.RequestHandler;
import com.rseye.util.Postable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;

public class PositionUpdate implements Postable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private WorldPoint position;

    public PositionUpdate(String username, WorldPoint position) {
        this.username = username;
        this.position = position;
    }

    @Override
    public RequestHandler.Endpoint endpoint() {
        return RequestHandler.Endpoint.POSITION_UPDATE;
    }
}
