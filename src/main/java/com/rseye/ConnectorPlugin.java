package com.rseye;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.rseye.io.RequestHandler;
import com.rseye.object.Position;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

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
	private Position playerLastPosition;

	@Override
	protected void startUp() throws Exception {
		log.info("rseye-connector started!");
		this.requestHandler = new RequestHandler(config);
		this.gson = new Gson();
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("rseye-connector stopped!");
		this.requestHandler = null;
		this.gson = null;
		this.hasTicked = false;
	}

	@Subscribe
	public void onGameTick(final GameTick event) {
		if(!hasTicked){
			hasTicked = true;
			playerLastPosition = new Position(client.getLocalPlayer().getName(), client.getLocalPlayer().getWorldLocation());
			return;
		}
		updatePlayerPosition();
	}

	public void updatePlayerPosition() {
		Player player = client.getLocalPlayer();
		Position position = new Position(player.getName(), player.getWorldLocation());
		if(!position.equals(playerLastPosition)) {
			requestHandler.execute(RequestHandler.Endpoint.PLAYER_POSITION, position.toJson());
			playerLastPosition = position;
		}
	}

	@Provides
	ConnectorConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ConnectorConfig.class);
	}
}
