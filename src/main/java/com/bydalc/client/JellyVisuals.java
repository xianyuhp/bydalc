package com.bydalc.client;

import com.bydalc.JellyClientState;
import com.bydalc.JellyConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * Applies the jelly wobble to the pose stack of an entity that stands inside the radius of a
 * jukebox playing the disc. Purely visual: neither the camera nor the server side position of
 * the entity is touched.
 */
public final class JellyVisuals {
    public static boolean shouldApply(Entity entity) {
        if (!JellyClientState.isAffected(entity)) {
            return false;
        }

        // Never wobble the body the camera is sitting in, otherwise the spinning limbs
        // would be visible in first person.
        Minecraft minecraft = Minecraft.getInstance();
        return entity != minecraft.getCameraEntity() || !minecraft.options.getCameraType().isFirstPerson();
    }

    public static void apply(Entity entity, float partialTick, PoseStack poseStack) {
        double time = entity.level().getGameTime() + partialTick;

        // Squash and stretch: the creature is flattened and then yanked back up.
        float wave = Mth.sin((float) (time * JellyConfig.wobbleRadiansPerTick + (entity.getId() & 15) * 0.4D));
        float scaleY = 1.0F + (float) JellyConfig.stretch * wave;
        float scaleXZ = 1.0F - (float) JellyConfig.squash * wave;
        float bounce = (float) JellyConfig.bounce * Math.max(0.0F, wave);

        // The spin always happens around the current visual centre of the creature. Modded
        // creatures often have a much larger model than hitbox, so the culling box - which mods
        // use to describe their real model size - is taken into account as well.
        float angle = (float) (time * JellyConfig.spinDegreesPerTick % 360.0D);
        float height = (float) Math.max(entity.getBbHeight(), entity.getBoundingBoxForCulling().getYsize());
        float halfHeight = height * 0.5F * scaleY;

        poseStack.translate(0.0F, bounce + halfHeight, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.translate(0.0F, -halfHeight, 0.0F);
        poseStack.scale(scaleXZ, scaleY, scaleXZ);
    }

    private JellyVisuals() {}
}