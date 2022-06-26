package com.rseye;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "rseye-connector")
public class Plugin extends net.runelite.client.plugins.Plugin {
	@Inject
	private Client client;
	@Inject
	private PluginConfig config;

	@Override
	protected void startUp() throws Exception {
		log.info("rseye-connector started!");
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("rseye-connector stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if(gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			log.debug("Login Detected");
		}
	}

	@Provides
	PluginConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(PluginConfig.class);
	}
}
