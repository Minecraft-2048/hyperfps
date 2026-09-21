package com.pinea.hyperfps.mixin.client;

import com.pinea.hyperfps.HyperFpsConfig;
import com.pinea.hyperfps.HyperFpsState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererCullingMixin<T extends Entity> {
	@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
	private void hyperfps$distanceCull(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.options == null) return;
		if (HyperFpsConfig.INSTANCE.disableLimitsWhenPaused && client.isPaused()) return;

		int viewDistanceChunks = HyperFpsState.capViewDistanceChunks(client.options.getViewDistance().getValue());
		double maxBlocks = viewDistanceChunks * 16.0 * Math.max(0.05, HyperFpsState.getEntityMultiplier());
		double maxSq = maxBlocks * maxBlocks;

		if (entity.squaredDistanceTo(x, y, z) > maxSq) {
			cir.setReturnValue(false);
		}
	}
}

