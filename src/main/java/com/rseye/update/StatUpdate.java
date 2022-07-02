package com.rseye.update;

import com.rseye.io.RequestHandler;
import com.rseye.util.Postable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.events.StatChanged;

import java.util.concurrent.CopyOnWriteArrayList;

public class StatUpdate implements Postable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private int combatLevel;

    @Getter
    @Setter
    private CopyOnWriteArrayList<StatChanged> statChanges;

    public StatUpdate(String username, int combatLevel, CopyOnWriteArrayList<StatChanged> statChanges) {
        this.username = username;
        this.combatLevel = combatLevel;
        this.statChanges = statChanges;
    }

    @Override
    public RequestHandler.Endpoint endpoint() {
        return RequestHandler.Endpoint.STAT_UPDATE;
    }
}
