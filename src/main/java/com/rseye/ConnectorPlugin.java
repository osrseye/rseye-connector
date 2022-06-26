package com.rseye;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.rseye.io.RequestHandler;
import com.rseye.update.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
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

	@Override
	protected void startUp() throws Exception {
		log.info("rseye-connector started!");
		this.requestHandler = new RequestHandler(config);
		this.gson = new Gson();
		this.lastStatUpdate = new CopyOnWriteArrayList<>();
		this.lastQuestStateUpdate = new CopyOnWriteArrayList<>();
		this.questStates = new ConcurrentHashMap<>();
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("rseye-connector stopped!");
		this.requestHandler = null;
		this.gson = null;
		this.hasTicked = false;
		this.lastStatUpdate = null;
	}

	@Subscribe
	public void onGameTick(final GameTick event) {
		if(!hasTicked){
			hasTicked = true;
			player = client.getLocalPlayer();
			lastPositionUpdate = new PositionUpdate(client.getLocalPlayer().getName(), client.getLocalPlayer().getWorldLocation());
			return;
		}
		processPositionUpdate();
		processStatUpdate(); // group together stat changes since there can be multiple per tick
		processQuestUpdate();
		processBankUpdate();
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged gameStateChanged) {
		if(config.loginData()) {
			int state = gameStateChanged.getGameState().getState();
			if(hasTicked && (state == 30 || state == 40)) {
				LoginUpdate loginUpdate = new LoginUpdate(player.getName(), state == 30 ? "LOGGED_IN" : "LOGGED_OUT");
				requestHandler.execute(RequestHandler.Endpoint.LOGIN_STATE, loginUpdate.toJson());
			}
		}
	}

	@Subscribe
	public void onStatChanged(final StatChanged statChanged) {
		if(config.statsData()) {
			lastStatUpdate.add(statChanged);
		}
	}

	@Subscribe
	public void onNpcLootReceived(final NpcLootReceived npcLootReceived) {
		if(config.lootData()) {
			LootUpdate lootUpdate = new LootUpdate(player.getName(), npcLootReceived);
			requestHandler.execute(RequestHandler.Endpoint.LOOT_UPDATE, lootUpdate.toJson());
		}
	}

	private void processPositionUpdate() {
		if(config.positionData()) {
			PositionUpdate positionUpdate = new PositionUpdate(player.getName(), player.getWorldLocation());
			if(!positionUpdate.equals(lastPositionUpdate)) {
				requestHandler.execute(RequestHandler.Endpoint.PLAYER_POSITION, positionUpdate.toJson());
				lastPositionUpdate = positionUpdate;
			}
		}
	}

	private void processStatUpdate() {
		if(config.statsData()) {
			if(!lastStatUpdate.isEmpty()) {
				StatUpdate statUpdate = new StatUpdate(player.getName(), player.getCombatLevel(), lastStatUpdate);
				requestHandler.execute(RequestHandler.Endpoint.STAT_CHANGE, gson.toJson(statUpdate));
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
				requestHandler.execute(RequestHandler.Endpoint.QUEST_CHANGE, new QuestUpdate(player.getName(), lastQuestStateUpdate).toJson());
				lastQuestStateUpdate.clear();
			}
		}
	}

	private void processBankUpdate() {
		if(client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER) != null) {
			isBankOpen = true;
			lastBankState = client.getItemContainer(InventoryID.BANK);
			return;
		}
		if(isBankOpen && lastBankState != null) {
			isBankOpen = false;
			List<Item> items = Arrays.asList(lastBankState.getItems());
			System.out.println(new BankUpdate(player.getName(), items).toJson());
			requestHandler.execute(RequestHandler.Endpoint.BANK_UPDATE, new BankUpdate(player.getName(), items).toJson());
		}
	}

	@Provides
	ConnectorConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ConnectorConfig.class);
	}
}
