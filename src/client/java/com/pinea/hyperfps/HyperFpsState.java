package com.pinea.hyperfps;

/**
 * Valeurs effectives (runtime) modifiées par le mode adaptatif.
 * Les mixins lisent ces valeurs pour appliquer les limites.
 */
public final class HyperFpsState {
	private static volatile float entityMultiplier = 1.0f;
	private static volatile float blockEntityMultiplier = 1.0f;
	private static volatile float particleRate = 1.0f;
	private static volatile int viewDistanceCapChunks = Integer.MAX_VALUE;

	private HyperFpsState() {}

	public static float getEntityMultiplier() {
		return entityMultiplier;
	}

	public static float getBlockEntityMultiplier() {
		return blockEntityMultiplier;
	}

	public static float getParticleRate() {
		return particleRate;
	}

	public static int capViewDistanceChunks(int vanilla) {
		return Math.min(vanilla, viewDistanceCapChunks);
	}

	static void resetToConfig() {
		HyperFpsConfig c = HyperFpsConfig.INSTANCE;
		entityMultiplier = clamp(c.entityRenderDistanceMultiplier, c.minEntityRenderDistanceMultiplier, c.entityRenderDistanceMultiplier);
		blockEntityMultiplier = clamp(c.blockEntityRenderDistanceMultiplier, c.minBlockEntityRenderDistanceMultiplier, c.blockEntityRenderDistanceMultiplier);
		particleRate = clamp(c.particleSpawnRate, c.minParticleSpawnRate, c.particleSpawnRate);
		viewDistanceCapChunks = Integer.MAX_VALUE;
	}

	static void setEntityMultiplier(float v) {
		entityMultiplier = v;
	}

	static void setBlockEntityMultiplier(float v) {
		blockEntityMultiplier = v;
	}

	static void setParticleRate(float v) {
		particleRate = v;
	}

	static void setViewDistanceCapChunks(int v) {
		viewDistanceCapChunks = v;
	}

	static float clamp(float v, float min, float max) {
		if (v < min) return min;
		if (v > max) return max;
		return v;
	}
}

