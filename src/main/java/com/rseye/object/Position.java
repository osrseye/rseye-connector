package com.rseye.object;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;

import java.util.Objects;

public class Position {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private WorldPoint position;

    public Position(String username, WorldPoint position){
        this.username = username;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Position p = (Position) o;
        return Objects.equals(username, p.username) && Objects.equals(position, p.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, position);
    }
}
