package com.rseye.update;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.events.StatChanged;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatUpdate extends Jsonable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private CopyOnWriteArrayList<StatChanged> statsChanged;

    public StatUpdate(String username, CopyOnWriteArrayList<StatChanged> statsChanged) {
        this.username = username;
        this.statsChanged = statsChanged;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        StatUpdate s = (StatUpdate) o;
        return Objects.equals(username, s.username) && Objects.equals(statsChanged, s.statsChanged);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, statsChanged);
    }
}
