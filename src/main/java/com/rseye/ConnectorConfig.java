package com.rseye;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("rseye-connector")
public interface ConnectorConfig extends Config {
	@ConfigItem(
			keyName = "Base Endpoint",
			name = "Endpoint",
			description = "Endpoint to send data to: (example: http://localhost/api/v1/)"
	)
	default String baseEndpoint() {
		return "http://localhost/api/v1/";
	}

	@ConfigItem(
		keyName = "Bearer Token",
		name = "bearerToken",
		description = "Token provided to endpoint"
	)
	default String bearerToken() {
		return "token";
	}

	@ConfigItem(
		position = 1,
		keyName = "Send Position Data",
		name = "Position Data",
		description = "Toggle to send/omit player position data"
	)
	default boolean positionData() {
		return true;
	}
}
