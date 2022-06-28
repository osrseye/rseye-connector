package com.rseye.update;

import com.rseye.util.Jsonable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;

import java.util.Objects;

public class PositionUpdate extends Jsonable {
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
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        PositionUpdate p = (PositionUpdate) o;
        return Objects.equals(username, p.username) && Objects.equals(position, p.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, position);
    }
}
