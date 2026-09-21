package com.pinea.hyperfps;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

/**
 * Ajuste progressivement les limites pour rester proche d'un FPS cible.
 * On privilégie des changements petits et fréquents plutôt que des gros sauts.
 */
public final class AdaptiveTuner {
	private static final int UPDATE_EVERY_TICKS = 10;

	private int tick;
	private double fpsEma = -1.0;

	private Integer userMaxViewDistanceChunks = null;

	public void onClientTick(MinecraftClient client) {
		HyperFpsConfig cfg = HyperFpsConfig.INSTANCE;
		if (!cfg.adaptiveEnabled) {
			HyperFpsState.resetToConfig();
			return;
		}

		if (cfg.disableLimitsWhenPaused && client.isPaused()) return;

		if (++tick % UPDATE_EVERY_TICKS != 0) return;

		int fpsNow = Math.max(0, client.getCurrentFps());
		if (fpsEma < 0) fpsEma = fpsNow;
		fpsEma = fpsEma * 0.80 + fpsNow * 0.20;

		double target = Math.max(10, cfg.targetFps);
		double low = target - Math.max(1, cfg.targetFpsHysteresis);
		double high = target + Math.max(1, cfg.targetFpsHysteresis);

		boolean tooLow = fpsEma < low;
		boolean tooHigh = fpsEma > high;
		if (!tooLow && !tooHigh) return;

		// pas trop agressif: on ajuste de petites quantités.
		float stepDown = 0.04f;
		float stepUp = 0.02f;

		if (tooLow) {
			HyperFpsState.setEntityMultiplier(
				HyperFpsState.clamp(HyperFpsState.getEntityMultiplier() - stepDown, cfg.minEntityRenderDistanceMultiplier, cfg.entityRenderDistanceMultiplier)
			);
			HyperFpsState.setBlockEntityMultiplier(
				HyperFpsState.clamp(HyperFpsState.getBlockEntityMultiplier() - stepDown, cfg.minBlockEntityRenderDistanceMultiplier, cfg.blockEntityRenderDistanceMultiplier)
			);
			HyperFpsState.setParticleRate(
				HyperFpsState.clamp(HyperFpsState.getParticleRate() - (stepDown * 1.2f), cfg.minParticleSpawnRate, cfg.particleSpawnRate)
			);
			if (cfg.allowAdaptiveViewDistance) {
				adjustViewDistance(client, -1, cfg.minViewDistanceChunks);
			}
		} else {
			HyperFpsState.setEntityMultiplier(
				HyperFpsState.clamp(HyperFpsState.getEntityMultiplier() + stepUp, cfg.minEntityRenderDistanceMultiplier, cfg.entityRenderDistanceMultiplier)
			);
			HyperFpsState.setBlockEntityMultiplier(
				HyperFpsState.clamp(HyperFpsState.getBlockEntityMultiplier() + stepUp, cfg.minBlockEntityRenderDistanceMultiplier, cfg.blockEntityRenderDistanceMultiplier)
			);
			HyperFpsState.setParticleRate(
				HyperFpsState.clamp(HyperFpsState.getParticleRate() + (stepUp * 1.2f), cfg.minParticleSpawnRate, cfg.particleSpawnRate)
			);
			if (cfg.allowAdaptiveViewDistance) {
				adjustViewDistance(client, +1, cfg.minViewDistanceChunks);
			}
		}
	}

	private void adjustViewDistance(MinecraftClient client, int deltaChunks, int minChunks) {
		GameOptions options = client.options;
		if (options == null) return;
		SimpleOption<Integer> viewDistance = options.getViewDistance();
		if (viewDistance == null) return;

		Integer current = viewDistance.getValue();
		if (current == null) return;

		if (userMaxViewDistanceChunks == null) {
			userMaxViewDistanceChunks = current;
		}

		int maxAllowed = userMaxViewDistanceChunks;
		int next = current + deltaChunks;
		if (next < minChunks) next = minChunks;
		if (next > maxAllowed) next = maxAllowed;

		if (next != current) {
			viewDistance.setValue(next);
		}

		// Pour les mixins, on “cap” aussi la distance utilisée dans les calculs.
		HyperFpsState.setViewDistanceCapChunks(next);
	}
}

