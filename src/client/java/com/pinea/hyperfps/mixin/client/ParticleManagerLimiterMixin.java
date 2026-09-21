package com.pinea.hyperfps.mixin.client;

import com.pinea.hyperfps.HyperFpsConfig;
import com.pinea.hyperfps.HyperFpsState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerLimiterMixin {
	@Inject(
		method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
		at = @At("HEAD"),
		cancellable = true
	)
	private void hyperfps$limitParticles(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Particle> cir) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null && HyperFpsConfig.INSTANCE.disableLimitsWhenPaused && client.isPaused()) return;

		double rate = Math.max(0.0, Math.min(1.0, HyperFpsState.getParticleRate()));
		if (rate >= 0.999) return;

		if (ThreadLocalRandom.current().nextDouble() > rate) {
			cir.setReturnValue(null);
		}
	}
}

