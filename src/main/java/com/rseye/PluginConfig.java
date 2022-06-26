package com.rseye;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("rseye-connector")
public interface PluginConfig extends Config
{
	@ConfigItem(
			keyName = "Base Endpoint",
			name = "endpoint",
			description = "Endpoint to send data to: (example: http://localhost/api/)"
	)
	default String apiEndpoint() {
		return "http://localhost/api/";
	}

	@ConfigItem(
		keyName = "Bearer Token",
		name = "bearerToken",
		description = "Token provided to endpoint"
	)
	default String bearerToken() {
		return "token";
	}
}
