package com.rseye.update;

import com.rseye.io.RequestHandler;
import com.rseye.util.Postable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameState;

public class LoginUpdate implements Postable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private GameState state;

    public LoginUpdate(String username, GameState state) {
        this.username = username;
        this.state = state;
    }

    @Override
    public RequestHandler.Endpoint endpoint() {
        return RequestHandler.Endpoint.LOGIN_UPDATE;
    }
}
