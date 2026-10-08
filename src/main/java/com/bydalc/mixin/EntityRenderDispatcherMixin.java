package com.bydalc.mixin;

import com.bydalc.client.JellyVisuals;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Wraps the actual entity render call so the jelly transform is applied to every entity
 * (living or not, renderer based on LivingEntityRenderer or not - the ender dragon included)
 * without leaking into the shadow or hitbox pass.
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    private static final String RENDER_DESC = "Lnet/minecraft/client/renderer/entity/EntityRenderer;"
            + "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;"
            + "Lnet/minecraft/client/renderer/MultiBufferSource;I)V";

    @Inject(method = "render", at = @At(value = "INVOKE", target = RENDER_DESC, shift = At.Shift.BEFORE))
    private void bydalc$beforeEntityRender(Entity entity, double x, double y, double z, float rotationYaw,
            float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (JellyVisuals.shouldApply(entity)) {
            poseStack.pushPose();
            JellyVisuals.apply(entity, partialTick, poseStack);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = RENDER_DESC, shift = At.Shift.AFTER))
    private void bydalc$afterEntityRender(Entity entity, double x, double y, double z, float rotationYaw,
            float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (JellyVisuals.shouldApply(entity)) {
            poseStack.popPose();
        }
    }
}