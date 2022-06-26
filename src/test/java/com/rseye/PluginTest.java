package com.rseye;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PluginTest {
	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(ConnectorPlugin.class);
		RuneLite.main(args);
	}
}