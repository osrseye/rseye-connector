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

	@ConfigItem(
			position = 2,
			keyName = "Send Login Update Data",
			name = "Login Update Data",
			description = "Toggle to send/omit login state data"
	)
	default boolean loginData() {
		return true;
	}

	@ConfigItem(
			position = 3,
			keyName = "Send Stats Update Data",
			name = "Stats Update Data",
			description = "Toggle to send/omit level/xp/boostedLevel data"
	)
	default boolean statsData() {
		return true;
	}

	@ConfigItem(
			position = 4,
			keyName = "Send Quest Update Data",
			name = "Quest Update Data",
			description = "Toggle to send/omit quest data"
	)
	default boolean questData() {
		return true;
	}

	@ConfigItem(
			position = 5,
			keyName = "Send Bank Update Data",
			name = "Bank Update Data",
			description = "Toggle to send/omit bank data"
	)
	default boolean bankData() {
		return true;
	}
}
