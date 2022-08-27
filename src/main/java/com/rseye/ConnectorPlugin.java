package com.rseye;

import com.google.inject.Provides;
import com.rseye.io.RequestHandler;
import com.rseye.update.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.OkHttpClient;

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
	private OkHttpClient okHttpClient;

	@Inject
	private ConnectorConfig config;

	@Inject
	private ItemManager itemManager;

	private boolean hasTicked;
	private int ticks = 0;
	private RequestHandler requestHandler;
	private Player player;
	private PositionUpdate lastPositionUpdate;
	private CopyOnWriteArrayList<StatChanged> lastStatUpdate;
	private CopyOnWriteArrayList<QuestUpdate.Quest> lastQuestStateUpdate;
	private ConcurrentHashMap<Integer, QuestUpdate.Quest> questStates;
	private ItemContainer lastBankState;
	private OverheadUpdate lastOverheadState;
	private SkullUpdate lastSkullState;
	private boolean isBankOpen = false;

	@Override
	protected void startUp() {
		log.info("rseye-connector started!");
		this.requestHandler = new RequestHandler(okHttpClient, config);
		this.lastStatUpdate = new CopyOnWriteArrayList<>();
		this.lastQuestStateUpdate = new CopyOnWriteArrayList<>();
		this.questStates = new ConcurrentHashMap<>();
		this.lastOverheadState = new OverheadUpdate("", null);
		this.lastSkullState = new SkullUpdate("", null);
	}

	@Override
	protected void shutDown() {
		log.info("rseye-connector stopped!");
		this.hasTicked = false;
		this.ticks = 0;
		this.requestHandler = null;
		this.lastStatUpdate = null;
		this.lastQuestStateUpdate = null;
		this.questStates = null;
		this.lastOverheadState = null;
		this.lastSkullState = null;
	}

	@Subscribe
	public void onGameTick(final GameTick tick) {
		if(!hasTicked || player == null){
			hasTicked = true;
			ticks = 0;
			player = client.getLocalPlayer();
			questStates = new ConcurrentHashMap<>(); // re-init quest states else the initial quest data will only ever be sent once, unlike other similar events which fire every time a "LOGGED_IN" event occurs
			return;
		}

		if(ticks % config.positionDataFrequency() == 0) {
			processPositionUpdate();
		}

		if(ticks % config.overheadDataFrequency() == 0) {
			processOverheadUpdate();
		}

		if(ticks % config.skullDataFrequency() == 0) {
			processSkullUpdate();
		}

		processStatUpdate(); // group together stat changes since there can be multiple per tick
		processQuestUpdate();
		processBankUpdate();

		ticks = ticks++ > 144000 ? 0 : ticks; // reset tick count after 24 hours - otherwise we'll run into an int overflow in roughly 45 years.
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged gsc) {
		if(gsc.getGameState() != GameState.LOGGED_IN && gsc.getGameState() != GameState.LOADING) {
			hasTicked = false; // necessary step to refresh the local player object
			return;
		}
		if(player == null || !config.loginData()) {
			return;
		}

		requestHandler.submit(new LoginUpdate(player.getName(), gsc.getGameState()));
	}

	@Subscribe
	public void onStatChanged(final StatChanged statChanged) {
		lastStatUpdate.add(statChanged);
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged icc) {
		if(icc.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY)) {
			processInventoryUpdate(icc); // process inventory
		}
		if(icc.getItemContainer() == client.getItemContainer(InventoryID.EQUIPMENT)) {
			processEquipmentUpdate(icc); // process equipment
		}
	}

	@Subscribe
	public void onNpcLootReceived(final NpcLootReceived npcLootReceived) {
		if(player == null || !config.lootData()) {
			return;
		}

		requestHandler.submit(new LootUpdate(player.getName(), npcLootReceived));
	}

	@Subscribe
	public void onPlayerLootReceived(final PlayerLootReceived playerLootReceived) {
		if(player == null || !config.lootData()) {
			return;
		}

		requestHandler.submit(new LootUpdate(player.getName(), playerLootReceived));
	}

	@Subscribe
	public void onActorDeath(final ActorDeath actorDeath) {
		if(player == null || !config.deathData()) {
			return;
		}

		if(actorDeath.getActor().getName() != null && actorDeath.getActor().getName().equals(player.getName())) {
			requestHandler.submit(new DeathUpdate(player.getName()));
		}
	}

	@Subscribe
	public void onWidgetLoaded(final WidgetLoaded widgetLoaded) {
		ItemContainer container;
		switch(widgetLoaded.getGroupId()) {
			case WidgetID.BARROWS_GROUP_ID:
				container = client.getItemContainer(InventoryID.BARROWS_REWARD);
				break;
			default:
				return;
		}

		if(container != null) {
			requestHandler.submit(new LootUpdate(player.getName(), widgetLoaded.getGroupId(), container));
		}
	}

	private void processPositionUpdate() {
		if(player == null || !config.positionData()) {
			return;
		}

		if(lastPositionUpdate == null || !player.getWorldLocation().equals(lastPositionUpdate.getPosition())) {
			requestHandler.submit(lastPositionUpdate = new PositionUpdate(player.getName(), player.getWorldLocation()));
		}
	}

	private void processStatUpdate() {
		if(player == null || !config.statsData()) {
			lastStatUpdate.clear(); // stops lastStateUpdate becoming an issue if config.statsData is false
			return;
		}

		if(!lastStatUpdate.isEmpty()) {
			requestHandler.submit(new StatUpdate(player.getName(), player.getCombatLevel(), lastStatUpdate));
			lastStatUpdate.clear();
		}
	}

	private void processQuestUpdate() {
		if(player == null || !config.questData()) {
			return;
		}

		for(Quest quest: Quest.values()) {
			QuestUpdate.Quest existingObject = questStates.getOrDefault(quest.getId(), null);
			QuestUpdate.Quest newObject = new QuestUpdate.Quest(quest.getId(), quest.getName(), quest.getState(client));
			if(existingObject == null || !existingObject.getState().equals(newObject.getState())) {
				questStates.put(quest.getId(), newObject);
				lastQuestStateUpdate.add(newObject);
			}
		}
		if(!lastQuestStateUpdate.isEmpty()) {
			requestHandler.submit(new QuestUpdate(player.getName(), client.getVar(VarPlayer.QUEST_POINTS), lastQuestStateUpdate));
			lastQuestStateUpdate.clear();
		}
	}

	private void processBankUpdate() {
		if(player == null || !config.bankData()) {
			return;
		}

		if(client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER) != null) {
			isBankOpen = true;
			lastBankState = client.getItemContainer(InventoryID.BANK);
			return;
		}
		if(isBankOpen && lastBankState != null) {
			isBankOpen = false;
			List<Item> items = Arrays.asList(lastBankState.getItems());
			requestHandler.submit(new BankUpdate(player.getName(), items));
		}
	}

	private void processInventoryUpdate(ItemContainerChanged icc) {
		if(player == null || !config.inventoryData()) {
			return;
		}

		List<Item> items = Arrays.asList(icc.getItemContainer().getItems());
		requestHandler.submit(new InventoryUpdate(player.getName(), items));
	}

	private void processEquipmentUpdate(ItemContainerChanged icc) {
		if(player == null || !config.equipmentData()) {
			return;
		}

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
		requestHandler.submit(new EquipmentUpdate(player.getName(), equipped));
	}

	private void processOverheadUpdate() {
		if(player == null || !config.overheadData()) {
			return;
		}

		if(lastOverheadState.getOverhead() != null && !lastOverheadState.getOverhead().equals(player.getOverheadIcon())
				|| lastOverheadState.getOverhead() == null && player.getOverheadIcon() != null) {
			requestHandler.submit(lastOverheadState = new OverheadUpdate(player.getName(), player.getOverheadIcon()));
		}
	}

	private void processSkullUpdate() {
		if(player == null || !config.skullData()) {
			return;
		}

		if(lastSkullState.getSkull() != null && !lastSkullState.getSkull().equals(player.getSkullIcon())
				|| lastSkullState.getSkull() == null && player.getSkullIcon() != null) {
			requestHandler.submit(lastSkullState = new SkullUpdate(player.getName(), player.getSkullIcon()));
		}
	}

	@Provides
	ConnectorConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ConnectorConfig.class);
	}
}
