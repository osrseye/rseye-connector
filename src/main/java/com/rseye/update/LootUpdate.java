package com.rseye.update;

import com.rseye.util.Jsonable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Item;
import net.runelite.client.events.NpcLootReceived;

import java.util.ArrayList;
import java.util.List;

public class LootUpdate extends Jsonable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private Integer npcId;

    @Getter
    @Setter
    private List<Item> items = new ArrayList<>();

    public LootUpdate(String username, NpcLootReceived npcLootReceived) {
        this.username = username;
        this.npcId = npcLootReceived.getNpc().getId();
        npcLootReceived.getItems().forEach(itemStack -> items.add(new Item(itemStack.getId(), itemStack.getQuantity())));
    }
}
