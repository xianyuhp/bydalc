package com.bydalc.client;

import com.bydalc.JellyClientState;
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
    private static final float SPIN_DEGREES_PER_TICK = 15.0F;
    private static final double WOBBLE_RADIANS_PER_TICK = 0.45D;
    private static final float STRETCH = 0.35F;
    private static final float SQUASH = 0.22F;
    private static final float BOUNCE = 0.15F;

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
        float wave = Mth.sin((float) (time * WOBBLE_RADIANS_PER_TICK + (entity.getId() & 15) * 0.4D));
        float scaleY = 1.0F + STRETCH * wave;
        float scaleXZ = 1.0F - SQUASH * wave;
        float bounce = BOUNCE * Math.max(0.0F, wave);

        // The spin always happens around the current visual centre of the creature. Modded
        // creatures often have a much larger model than hitbox, so the culling box - which mods
        // use to describe their real model size - is taken into account as well.
        float angle = (float) (time * SPIN_DEGREES_PER_TICK % 360.0D);
        float height = (float) Math.max(entity.getBbHeight(), entity.getBoundingBoxForCulling().getYsize());
        float halfHeight = height * 0.5F * scaleY;

        poseStack.translate(0.0F, bounce + halfHeight, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.translate(0.0F, -halfHeight, 0.0F);
        poseStack.scale(scaleXZ, scaleY, scaleXZ);
    }

    private JellyVisuals() {}
}