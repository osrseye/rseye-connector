package com.rseye.update;

import com.rseye.util.Jsonable;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class DeathUpdate extends Jsonable {
    @Getter
    @Setter
    private String username;

    public DeathUpdate(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        DeathUpdate that = (DeathUpdate) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
