package com.rseye.update;

import com.rseye.io.RequestHandler;
import com.rseye.util.Postable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
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
        if(type == InventoryID.BARROWS_REWARD.getId()) {
            this.lootType = "Barrows";
            this.items.addAll(Arrays.asList(itemContainer.getItems()));
            return;
        }
        if(type == InventoryID.CHAMBERS_OF_XERIC_CHEST.getId()) {
            this.lootType = "Chambers of Xeric";
            this.items.addAll(Arrays.asList(itemContainer.getItems()));
            return;
        }
        if(type == InventoryID.THEATRE_OF_BLOOD_CHEST.getId()) {
            this.lootType = "Theatre of Blood";
            this.items.addAll(Arrays.asList(itemContainer.getItems()));
            return;
        }
        if(type == InventoryID.TOA_REWARD_CHEST.getId()) {
            this.lootType = "Tombs of Amascut";
            this.items.addAll(Arrays.asList(itemContainer.getItems()));
            return;
        }
    }

    @Override
    public RequestHandler.Endpoint endpoint() {
        return RequestHandler.Endpoint.LOOT_UPDATE;
    }
}
