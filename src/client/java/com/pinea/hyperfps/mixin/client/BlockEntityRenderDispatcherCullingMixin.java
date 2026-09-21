package com.pinea.hyperfps.mixin.client;

import com.pinea.hyperfps.HyperFpsConfig;
import com.pinea.hyperfps.HyperFpsState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherCullingMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void hyperfps$distanceCull(BlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.options == null || client.world == null) return;
		if (HyperFpsConfig.INSTANCE.disableLimitsWhenPaused && client.isPaused()) return;

		int viewDistanceChunks = HyperFpsState.capViewDistanceChunks(client.options.getViewDistance().getValue());
		double maxBlocks = viewDistanceChunks * 16.0 * Math.max(0.05, HyperFpsState.getBlockEntityMultiplier());
		double maxSq = maxBlocks * maxBlocks;

		Vec3d cam = client.gameRenderer.getCamera().getPos();
		BlockPos pos = blockEntity.getPos();
		Vec3d center = Vec3d.ofCenter(pos);
		if (center.squaredDistanceTo(cam) > maxSq) {
			ci.cancel();
		}
	}
}

