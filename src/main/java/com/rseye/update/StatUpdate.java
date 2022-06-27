package com.rseye.update;

import com.rseye.util.Jsonable;
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
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        StatUpdate that = (StatUpdate) o;
        return combatLevel == that.combatLevel && Objects.equals(username, that.username) && Objects.equals(statChanges, that.statChanges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, statChanges);
    }
}
