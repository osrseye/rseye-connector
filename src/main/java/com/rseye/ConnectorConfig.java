package com.rseye;

import net.runelite.client.config.*;

@ConfigGroup("rseye-connector")
public interface ConnectorConfig extends Config {
	@ConfigSection(
			position = 0,
			name = "Endpoint Configuration",
			description = "Endpoint POST configuration"
	)
	String apiConfig = "apiConfig";

	@ConfigSection(
			position = 1,
			name = "Data Toggles",
			description = "Enable/Disable which data is sent"
	)
	String dataToggles = "dataToggles";

	@ConfigSection(
			position = 2,
			name = "Data Frequency",
			description = "Set the frequency that data is sent"
	)
	String dataFrequency = "dataFrequency";

	@ConfigItem(
			position = 0,
			keyName = "Base Endpoint",
			name = "Endpoint",
			description = "Endpoint to send data to: (example: http://localhost/api/v1/)",
			section = apiConfig
	)
	default String baseEndpoint() {
		return "http://localhost/api/v1/";
	}

	@ConfigItem(
			position = 1,
			keyName = "Bearer Token",
			name = "bearerToken",
			description = "Token provided to endpoint",
			section = apiConfig
	)
	default String bearerToken() {
		return "token";
	}

	@ConfigItem(
			position = 0,
			keyName = "Send Position Data",
			name = "Position Data",
			description = "Toggle to send/omit player position data",
			section = dataToggles
	)
	default boolean positionData() {
		return true;
	}

	@ConfigItem(
			position = 1,
			keyName = "Send Login Data",
			name = "Login Data",
			description = "Toggle to send/omit login state data",
			section = dataToggles
	)
	default boolean loginData() {
		return true;
	}

	@ConfigItem(
			position = 2,
			keyName = "Send Stat Data",
			name = "Stat Data",
			description = "Toggle to send/omit level-xp-boostedLevel data",
			section = dataToggles
	)
	default boolean statsData() {
		return true;
	}

	@ConfigItem(
			position = 3,
			keyName = "Send Quest Data",
			name = "Quest Data",
			description = "Toggle to send/omit quest data",
			section = dataToggles
	)
	default boolean questData() {
		return true;
	}

	@ConfigItem(
			position = 4,
			keyName = "Send Bank Data",
			name = "Bank Data",
			description = "Toggle to send/omit bank data",
			section = dataToggles
	)
	default boolean bankData() {
		return true;
	}

	@ConfigItem(
			position = 5,
			keyName = "Send Loot Data",
			name = "Loot Data",
			description = "Toggle to send/omit loot data",
			section = dataToggles
	)
	default boolean lootData() {
		return true;
	}

	@ConfigItem(
			position = 6,
			keyName = "Send Inventory Data",
			name = "Inventory Data",
			description = "Toggle to send/omit inventory data",
			section = dataToggles
	)
	default boolean inventoryData() {
		return true;
	}

	@ConfigItem(
			position = 7,
			keyName = "Send Equipment Data",
			name = "Equipment Data",
			description = "Toggle to send/omit equipment data",
			section = dataToggles
	)
	default boolean equipmentData() {
		return true;
	}

	@ConfigItem(
			position = 8,
			keyName = "Send Death Data",
			name = "Death Data",
			description = "Toggle to send/omit death data",
			section = dataToggles
	)
	default boolean deathData() {
		return true;
	}

	@ConfigItem(
			position = 9,
			keyName = "Send Overhead Data",
			name = "Overhead Data",
			description = "Toggle to send/omit player overhead icon data",
			section = dataToggles
	)
	default boolean overheadData() {
		return true;
	}

	@ConfigItem(
			position = 10,
			keyName = "Send Skull Data",
			name = "Skull Data",
			description = "Toggle to send/omit player skull icon data",
			section = dataToggles
	)
	default boolean skullData() {
		return true;
	}

	@ConfigItem(
			position = 0,
			keyName = "Position Data Frequency",
			name = "Position Data",
			description = "How often to send position data",
			section = dataFrequency
	)
	@Units(Units.TICKS)
	default int positionDataFrequency() {
		return 1;
	}

	@ConfigItem(
			position = 1,
			keyName = "Overhead Data Frequency",
			name = "Overhead Data",
			description = "How often to send overhead icon data",
			section = dataFrequency
	)
	@Units(Units.TICKS)
	default int overheadDataFrequency() {
		return 1;
	}

	@ConfigItem(
			position = 2,
			keyName = "Skull Data Frequency",
			name = "Skull Data",
			description = "How often to send skull icon data",
			section = dataFrequency
	)
	@Units(Units.TICKS)
	default int skullDataFrequency() {
		return 1;
	}
}
