package com.pinea.hyperfps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HyperFpsConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("hyperfps.json");

	public static HyperFpsConfig INSTANCE = new HyperFpsConfig();

	/**
	 * Active le mode adaptatif. Quand activé, le mod ajuste automatiquement les limites
	 * pour rester proche de {@link #targetFps}.
	 */
	public boolean adaptiveEnabled = true;

	/**
	 * FPS cible. Si ton objectif est "+10 FPS", mets un nombre réaliste pour ta machine
	 * (ex: 60, 90, 120).
	 */
	public int targetFps = 60;

	/**
	 * Marge (hystérésis) pour éviter les oscillations autour de la cible.
	 */
	public int targetFpsHysteresis = 6;

	/**
	 * Multiplie la distance maximale de rendu des entités. 1.0 = vanilla (basé sur la view distance).
	 * Baisser cette valeur a un énorme impact FPS en zones chargées.
	 */
	public float entityRenderDistanceMultiplier = 0.75f; // valeur max (quand ça va bien)

	/**
	 * Valeur minimale autorisée pour le mode adaptatif.
	 */
	public float minEntityRenderDistanceMultiplier = 0.35f;

	/**
	 * Multiplie la distance maximale de rendu des block entities (coffres, fours, etc.).
	 */
	public float blockEntityRenderDistanceMultiplier = 0.65f; // valeur max
	public float minBlockEntityRenderDistanceMultiplier = 0.30f;

	/**
	 * 1.0 = vanilla. 0.5 = ~50% des particules sont droppées (aléatoirement).
	 */
	public float particleSpawnRate = 0.6f; // valeur max
	public float minParticleSpawnRate = 0.15f;

	/**
	 * Autorise le mod à baisser/monter la view distance client automatiquement (gros gain FPS).
	 * Si false, le mod ne touche pas à ce réglage.
	 */
	public boolean allowAdaptiveViewDistance = true;

	/**
	 * View distance minimale (en chunks) que le mod peut imposer.
	 */
	public int minViewDistanceChunks = 4;

	/**
	 * Quand true, ne limite rien si tu es dans un menu (utile pour screenshots).
	 */
	public boolean disableLimitsWhenPaused = true;

	public static void load() {
		try {
			if (!Files.exists(PATH)) {
				save();
				return;
			}
			String raw = Files.readString(PATH, StandardCharsets.UTF_8);
			HyperFpsConfig cfg = GSON.fromJson(raw, HyperFpsConfig.class);
			if (cfg != null) INSTANCE = cfg;
		} catch (IOException | JsonSyntaxException ignored) {
			INSTANCE = new HyperFpsConfig();
			try {
				save();
			} catch (IOException ignored2) {
				// best-effort
			}
		}
	}

	public static void save() throws IOException {
		Files.createDirectories(PATH.getParent());
		Files.writeString(PATH, GSON.toJson(INSTANCE), StandardCharsets.UTF_8);
	}

	private HyperFpsConfig() {}
}

