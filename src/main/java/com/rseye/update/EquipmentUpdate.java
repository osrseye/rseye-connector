package com.rseye.update;

import com.rseye.io.RequestHandler;
import com.rseye.util.Postable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;

import java.util.HashMap;

public class EquipmentUpdate implements Postable {
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
    public RequestHandler.Endpoint endpoint() {
        return RequestHandler.Endpoint.EQUIPMENT_UPDATE;
    }
}
