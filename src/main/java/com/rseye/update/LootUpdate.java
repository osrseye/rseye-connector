package com.rseye.update;

import com.rseye.io.RequestHandler;
import com.rseye.util.Postable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Item;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;

import java.util.ArrayList;
import java.util.List;

public class LootUpdate implements Postable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String lootType;

    @Getter
    @Setter
    private Integer entityId;

    @Getter
    @Setter
    private String entityName;

    @Getter
    @Setter
    private List<Item> items = new ArrayList<>();

    public LootUpdate(String username, NpcLootReceived npcLootReceived) {
        this.username = username;
        this.lootType = "npc";
        this.entityId = npcLootReceived.getNpc().getId();
        this.entityName = npcLootReceived.getNpc().getName();
        npcLootReceived.getItems().forEach(itemStack -> items.add(new Item(itemStack.getId(), itemStack.getQuantity())));
    }

    public LootUpdate(String username, PlayerLootReceived playerLootReceived) {
        this.username = username;
        this.lootType = "player";
        this.entityId = playerLootReceived.getPlayer().getId();
        this.entityName = playerLootReceived.getPlayer().getName();
        playerLootReceived.getItems().forEach(itemStack -> items.add(new Item(itemStack.getId(), itemStack.getQuantity())));
    }

    @Override
    public RequestHandler.Endpoint endpoint() {
        return RequestHandler.Endpoint.LOOT_UPDATE;
    }
}
