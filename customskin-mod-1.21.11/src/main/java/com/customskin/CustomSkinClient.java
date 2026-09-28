package com.customskin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CustomSkinClient implements ClientModInitializer {
	public static final String MOD_ID = "customskin";

	@Override
	public void onInitializeClient() {
		Path dir = SkinManager.folder();
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			System.err.println("[customskin] Could not create folder " + dir + ": " + e);
		}
		System.out.println("[customskin] Put your skin PNG in: " + dir.toAbsolutePath());
	}
}
