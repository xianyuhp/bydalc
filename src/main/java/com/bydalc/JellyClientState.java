package com.bydalc;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Client side mirror of the jukeboxes that are currently playing the disc.
 * Contains no client-only types so it can safely be loaded on either side.
 */
public final class JellyClientState {
    private static final double RANGE_SQR = 10.0D * 10.0D;

    private static List<BlockPos> jukeboxes = List.of();
    private static Level trackedLevel;

    public static void accept(List<BlockPos> positions) {
        jukeboxes = List.copyOf(positions);
    }

    public static void clear() {
        jukeboxes = List.of();
    }

    /**
     * Called for every entity that is about to be rendered. Only {@link LivingEntity} counts as a
     * creature, which is what every mob of every mod extends, so modded mobs are covered as well.
     * The distance is measured against the hitbox instead of its centre, so oversized modded
     * creatures (dragons, giants, ...) still count while they stand next to the jukebox.
     */
    public static boolean isAffected(Entity entity) {
        if (jukeboxes.isEmpty() || !(entity instanceof LivingEntity)) {
            return false;
        }

        Level level = entity.level();
        if (trackedLevel != null && trackedLevel != level) {
            jukeboxes = List.of();
            trackedLevel = level;
            return false;
        }
        trackedLevel = level;

        AABB box = entity.getBoundingBox();
        for (BlockPos pos : jukeboxes) {
            if (box.distanceToSqr(Vec3.atCenterOf(pos)) <= RANGE_SQR) {
                return true;
            }
        }
        return false;
    }

    private JellyClientState() {}
}