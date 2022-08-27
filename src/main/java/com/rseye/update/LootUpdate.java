package com.rseye.update;

import com.rseye.io.RequestHandler;
import com.rseye.util.Postable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;

import java.util.ArrayList;
import java.util.Arrays;
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
        this.lootType = "NPC";
        this.entityId = npcLootReceived.getNpc().getId();
        this.entityName = npcLootReceived.getNpc().getName();
        npcLootReceived.getItems().forEach(itemStack -> this.items.add(new Item(itemStack.getId(), itemStack.getQuantity())));
    }

    public LootUpdate(String username, PlayerLootReceived playerLootReceived) {
        this.username = username;
        this.lootType = "Player";
        this.entityId = playerLootReceived.getPlayer().getId();
        this.entityName = playerLootReceived.getPlayer().getName();
        playerLootReceived.getItems().forEach(itemStack -> this.items.add(new Item(itemStack.getId(), itemStack.getQuantity())));
    }

    public LootUpdate(String username, int type, ItemContainer itemContainer) {
        this.username = username;
        switch(type) {
            case WidgetID.BARROWS_GROUP_ID:
                this.lootType = "Barrows";
                this.items.addAll(Arrays.asList(itemContainer.getItems()));
                break;
        }
    }

    @Override
    public RequestHandler.Endpoint endpoint() {
        return RequestHandler.Endpoint.LOOT_UPDATE;
    }
}
