package com.rseye.update;

import com.rseye.util.Jsonable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;

import java.util.HashMap;
import java.util.Objects;

public class EquipmentUpdate extends Jsonable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private HashMap<EquipmentInventorySlot, Item> items;

    public EquipmentUpdate(String username, HashMap<EquipmentInventorySlot, Item> items) {
        this.username = username;
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        EquipmentUpdate that = (EquipmentUpdate) o;
        return Objects.equals(username, that.username) && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, items);
    }
}
