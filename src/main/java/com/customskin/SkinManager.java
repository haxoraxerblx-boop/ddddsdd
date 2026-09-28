package com.customskin;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/** Loads the first .png in <gamedir>/customskin and registers it as a texture. */
public final class SkinManager {
	private static final Identifier TEXTURE_ID = Identifier.fromNamespaceAndPath("customskin", "player_skin");

	private static Path loadedFile;
	private static long loadedModified = -1;
	private static long lastCheck = 0;
	private static boolean loaded = false;
	private static boolean slim = false;

	private SkinManager() {}

	public static Path folder() {
		return FabricLoader.getInstance().getGameDir().resolve("customskin");
	}

	public static boolean isLoaded() { return loaded; }
	public static boolean isSlim() { return slim; }
	public static Identifier textureId() { return TEXTURE_ID; }

	/** Called from the render thread; re-checks the folder at most every 2 seconds so you can swap skins live. */
	public static void refreshIfNeeded() {
		long now = System.currentTimeMillis();
		if (now - lastCheck < 2000) return;
		lastCheck = now;

		Path png = findPng();
		if (png == null) {
			loaded = false;
			loadedFile = null;
			return;
		}
		try {
			long modified = Files.getLastModifiedTime(png).toMillis();
			if (loaded && png.equals(loadedFile) && modified == loadedModified) return;

			NativeImage image;
			try (InputStream in = Files.newInputStream(png)) {
				image = NativeImage.read(in);
			}
			if (image.getWidth() != 64 || image.getHeight() != 64) {
				System.err.println("[customskin] Skin must be 64x64 (got " + image.getWidth() + "x" + image.getHeight() + "). Ignoring.");
				image.close();
				loaded = false;
				return;
			}
			DynamicTexture texture = new DynamicTexture(() -> "customskin", image);
			Minecraft.getInstance().getTextureManager().register(TEXTURE_ID, texture);

			slim = png.getFileName().toString().toLowerCase().contains("slim");
			loadedFile = png;
			loadedModified = modified;
			loaded = true;
			System.out.println("[customskin] Loaded " + png.getFileName() + (slim ? " (slim arms)" : " (classic arms)"));
		} catch (Exception e) {
			System.err.println("[customskin] Failed to load skin: " + e);
			loaded = false;
		}
	}

	private static Path findPng() {
		Path dir = folder();
		if (!Files.isDirectory(dir)) return null;
		try (Stream<Path> s = Files.list(dir)) {
			return s.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".png"))
					.sorted().findFirst().orElse(null);
		} catch (Exception e) {
			return null;
		}
	}
}
