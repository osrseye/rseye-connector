package com.rseye;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.rseye.io.RequestHandler;
import com.rseye.update.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@PluginDescriptor(name = "rseye-connector")
public class ConnectorPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private ConnectorConfig config;

	@Inject
	private ItemManager itemManager;

	private RequestHandler requestHandler;
	private Gson gson;
	private boolean hasTicked;
	private Player player;
	private PositionUpdate lastPositionUpdate;
	private CopyOnWriteArrayList<StatChanged> lastStatUpdate;
	private CopyOnWriteArrayList<QuestUpdate.Quest> lastQuestStateUpdate;
	private ConcurrentHashMap<Integer, QuestUpdate.Quest> questStates;
	private ItemContainer lastBankState;
	private boolean isBankOpen = false;
	private GameState lastLoginState;

	@Override
	protected void startUp() throws Exception {
		log.info("rseye-connector started!");
		this.requestHandler = new RequestHandler(config);
		this.gson = new Gson();
		this.lastStatUpdate = new CopyOnWriteArrayList<>();
		this.lastQuestStateUpdate = new CopyOnWriteArrayList<>();
		this.questStates = new ConcurrentHashMap<>();
		this.lastLoginState = GameState.UNKNOWN;
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("rseye-connector stopped!");
		this.requestHandler = null;
		this.gson = null;
		this.hasTicked = false;
		this.lastStatUpdate = null;
		this.lastQuestStateUpdate = null;
		this.questStates = null;
		this.lastLoginState = null;
	}

	@Subscribe
	public void onGameTick(final GameTick gameTick) {
		if(!hasTicked || player == null){
			hasTicked = true;
			player = client.getLocalPlayer();
			questStates = new ConcurrentHashMap<>(); // re-init quest states else the initial quest data will only ever be sent once, unlike other similar events which fire every time a "LOGGED_IN" event occurs
			return;
		}
		processPositionUpdate();
		processStatUpdate(); // group together stat changes since there can be multiple per tick
		processQuestUpdate();
		processBankUpdate();
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged gameStateChanged) {
		if(gameStateChanged.getGameState() != GameState.LOGGED_IN) {
			// necessary step to refresh the local player object
			hasTicked = false;
		}

		if(player == null) return;
		if(config.loginData()) {
			LoginUpdate loginUpdate = new LoginUpdate(player.getName(), gameStateChanged.getGameState());
			requestHandler.execute(RequestHandler.Endpoint.LOGIN_UPDATE, loginUpdate.toJson());
		}
	}

	@Subscribe
	public void onStatChanged(final StatChanged statChanged) {
		// no player null check needed since the events are batched and sent after tick 0
		if(config.statsData()) {
			lastStatUpdate.add(statChanged);
		}
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged itemContainerChanged) {
		if(player == null) return;

		// process inventory
		if(itemContainerChanged.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY)) {
			processInventoryUpdate(itemContainerChanged);
		}
		// process equipment
		if(itemContainerChanged.getItemContainer() == client.getItemContainer(InventoryID.EQUIPMENT)) {
			processEquipmentUpdate(itemContainerChanged);
		}
	}

	@Subscribe
	public void onNpcLootReceived(final NpcLootReceived npcLootReceived) {
		if(player == null) return;
		if(config.lootData()) {
			LootUpdate lootUpdate = new LootUpdate(player.getName(), npcLootReceived);
			requestHandler.execute(RequestHandler.Endpoint.LOOT_UPDATE, lootUpdate.toJson());
		}
	}

	private void processPositionUpdate() {
		if(config.positionData()) {
			PositionUpdate positionUpdate = new PositionUpdate(player.getName(), player.getWorldLocation());
			if(!positionUpdate.equals(lastPositionUpdate)) {
				requestHandler.execute(RequestHandler.Endpoint.POSITION_UPDATE, positionUpdate.toJson());
				lastPositionUpdate = positionUpdate;
			}
		}
	}

	private void processStatUpdate() {
		if(config.statsData()) {
			if(!lastStatUpdate.isEmpty()) {
				StatUpdate statUpdate = new StatUpdate(player.getName(), player.getCombatLevel(), lastStatUpdate);
				requestHandler.execute(RequestHandler.Endpoint.STAT_UPDATE, statUpdate.toJson());
				lastStatUpdate.clear();
			}
		}
	}

	private void processQuestUpdate() {
		if(config.questData()) {
			for(Quest quest: Quest.values()) {
				QuestUpdate.Quest existingObject = questStates.getOrDefault(quest.getId(), null);
				QuestUpdate.Quest newObject = new QuestUpdate.Quest(quest.getId(), quest.getName(), quest.getState(client));
				if(existingObject == null || !existingObject.getState().equals(newObject.getState())) {
					questStates.put(quest.getId(), newObject);
					lastQuestStateUpdate.add(newObject);
				}
			}
			if(!lastQuestStateUpdate.isEmpty()) {
				QuestUpdate questUpdate = new QuestUpdate(player.getName(), client.getVar(VarPlayer.QUEST_POINTS), lastQuestStateUpdate);
				requestHandler.execute(RequestHandler.Endpoint.QUEST_UPDATE, questUpdate.toJson());
				lastQuestStateUpdate.clear();
			}
		}
	}

	private void processBankUpdate() {
		if(config.bankData()) {
			if(client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER) != null) {
				isBankOpen = true;
				lastBankState = client.getItemContainer(InventoryID.BANK);
				return;
			}
			if(isBankOpen && lastBankState != null) {
				isBankOpen = false;
				List<Item> items = Arrays.asList(lastBankState.getItems());
				BankUpdate bankUpdate = new BankUpdate(player.getName(), items);
				requestHandler.execute(RequestHandler.Endpoint.BANK_UPDATE, bankUpdate.toJson());
			}
		}
	}

	private void processInventoryUpdate(ItemContainerChanged icc) {
		if(config.inventoryData()) {
			List<Item> items = Arrays.asList(icc.getItemContainer().getItems());
			InventoryUpdate inventoryUpdate = new InventoryUpdate(player.getName(), items);
			requestHandler.execute(RequestHandler.Endpoint.INVENTORY_UPDATE, inventoryUpdate.toJson());
		}
	}

	private void processEquipmentUpdate(ItemContainerChanged icc) {
		if(config.equipmentData()) {
			List<Item> items = Arrays.asList(icc.getItemContainer().getItems());
			HashMap<EquipmentInventorySlot, Item> equipped = new HashMap<>();

			for(EquipmentInventorySlot equipmentInventorySlot : EquipmentInventorySlot.values()) {
				if(equipmentInventorySlot.getSlotIdx() < items.size()) {
					Item item = items.get(equipmentInventorySlot.getSlotIdx());
					if(item.getId() > -1 && item.getQuantity() > 0) {
						equipped.put(equipmentInventorySlot, item);
					}
				}
			}

			EquipmentUpdate equipmentUpdate = new EquipmentUpdate(player.getName(), equipped);
			requestHandler.execute(RequestHandler.Endpoint.EQUIPMENT_UPDATE, equipmentUpdate.toJson());
		}
	}

	@Provides
	ConnectorConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ConnectorConfig.class);
	}
}
