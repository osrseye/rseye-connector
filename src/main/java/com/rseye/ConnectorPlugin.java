package com.rseye;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.rseye.io.RequestHandler;
import com.rseye.object.Login;
import com.rseye.object.Position;
import com.rseye.object.QuestChanges;
import com.rseye.object.StatChanges;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Quest;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
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
	private Position lastPlayerPosition;
	private CopyOnWriteArrayList<StatChanged> lastStatChanges;
	private CopyOnWriteArrayList<QuestChanges.Quest> lastQuestStateChanges;
	private ConcurrentHashMap<Integer, QuestChanges.Quest> questStates;

	@Override
	protected void startUp() throws Exception {
		log.info("rseye-connector started!");
		this.requestHandler = new RequestHandler(config);
		this.gson = new Gson();
		this.lastStatChanges = new CopyOnWriteArrayList<>();
		this.lastQuestStateChanges = new CopyOnWriteArrayList<>();
		this.questStates = new ConcurrentHashMap<>();
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("rseye-connector stopped!");
		this.requestHandler = null;
		this.gson = null;
		this.hasTicked = false;
		this.lastStatChanges = null;
	}

	@Subscribe
	public void onGameTick(final GameTick event) {
		if(!hasTicked){
			hasTicked = true;
			player = client.getLocalPlayer();
			lastPlayerPosition = new Position(client.getLocalPlayer().getName(), client.getLocalPlayer().getWorldLocation());
			return;
		}
		updatePlayerPosition();
		updateLastTickStatChanges(); // group together stat changes since there can be multiple per tick
		updateQuestStates();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if(config.loginData()) {
			int state = gameStateChanged.getGameState().getState();
			if(hasTicked && (state == 30 || state == 40)) {
				Login login = new Login(player.getName(), player.getCombatLevel(), state == 30 ? "LOGGED_IN" : "LOGGED_OUT");
				requestHandler.execute(RequestHandler.Endpoint.LOGIN_STATE, login.toJson());
			}
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged) {
		if(config.statsData()) {
			lastStatChanges.add(statChanged);
		}
	}

	private void updatePlayerPosition() {
		if(config.positionData()) {
			Position position = new Position(player.getName(), player.getWorldLocation());
			if(!position.equals(lastPlayerPosition)) {
				requestHandler.execute(RequestHandler.Endpoint.PLAYER_POSITION, position.toJson());
				lastPlayerPosition = position;
			}
		}
	}

	private void updateLastTickStatChanges() {
		if(config.statsData()) {
			if(!lastStatChanges.isEmpty()) {
				StatChanges statChanges = new StatChanges(player.getName(), lastStatChanges);
				requestHandler.execute(RequestHandler.Endpoint.STAT_CHANGE, gson.toJson(statChanges));
				lastStatChanges.clear();
			}
		}
	}

	private void updateQuestStates() {
		if(config.questData()) {
			for(Quest quest: Quest.values()) {
				QuestChanges.Quest existingObject = questStates.getOrDefault(quest.getId(), null);
				QuestChanges.Quest newObject = new QuestChanges.Quest(quest.getId(), quest.getName(), quest.getState(client));
				if(existingObject == null || !existingObject.getState().equals(newObject.getState())) {
					questStates.put(quest.getId(), newObject);
					lastQuestStateChanges.add(newObject);
				}
			}
			if(!lastQuestStateChanges.isEmpty()) {
				requestHandler.execute(RequestHandler.Endpoint.QUEST_CHANGE, new QuestChanges(player.getName(), lastQuestStateChanges).toJson());
				lastQuestStateChanges.clear();
			}
		}
	}

	@Provides
	ConnectorConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ConnectorConfig.class);
	}
}
