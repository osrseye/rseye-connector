package com.rseye.update;

import com.rseye.util.Jsonable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Item;

import java.util.List;
import java.util.Objects;

public class InventoryUpdate extends Jsonable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private List<Item> items;

    public InventoryUpdate(String username, List<Item> items) {
        this.username = username;
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        InventoryUpdate that = (InventoryUpdate) o;
        return Objects.equals(username, that.username) && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, items);
    }
}