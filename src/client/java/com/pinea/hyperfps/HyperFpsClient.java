package com.pinea.hyperfps;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class HyperFpsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HyperFpsConfig.load();
		HyperFpsState.resetToConfig();

		AdaptiveTuner tuner = new AdaptiveTuner();
		ClientTickEvents.END_CLIENT_TICK.register(tuner::onClientTick);
	}
}

