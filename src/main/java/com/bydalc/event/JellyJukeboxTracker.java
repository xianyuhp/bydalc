package com.bydalc.event;

import com.bydalc.network.JellyNetwork;
import com.bydalc.registry.ModItems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Keeps track of the jukeboxes that are currently playing the jelly disc and forwards their
 * positions to every client in the dimension, so the wobble can be rendered there.
 */
public final class JellyJukeboxTracker {
    private static final Map<ResourceKey<Level>, Set<BlockPos>> ACTIVE = new HashMap<>();
    private static final Map<ResourceKey<Level>, List<BlockPos>> LAST_SENT = new HashMap<>();

    public static void onGameEvent(VanillaGameEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (event.getVanillaEvent().value() != GameEvent.JUKEBOX_PLAY.value()) {
            return;
        }

        BlockPos pos = BlockPos.containing(event.getEventPosition());
        if (isPlayingOurDisc(level, pos)) {
            ACTIVE.computeIfAbsent(level.dimension(), key -> new HashSet<>()).add(pos);
        }
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        boolean heartbeat = server.getTickCount() % 20 == 0;

        for (ServerLevel level : server.getAllLevels()) {
            ResourceKey<Level> key = level.dimension();
            Set<BlockPos> active = ACTIVE.get(key);

            if (active != null) {
                active.removeIf(pos -> !isPlayingOurDisc(level, pos));
                if (active.isEmpty()) {
                    ACTIVE.remove(key);
                    active = null;
                }
            }

            List<BlockPos> snapshot = active == null ? List.of() : new ArrayList<>(active);
            boolean changed = !snapshot.equals(LAST_SENT.get(key));
            if (changed || (heartbeat && !snapshot.isEmpty())) {
                LAST_SENT.put(key, snapshot);
                PacketDistributor.sendToPlayersInDimension(level, new JellyNetwork.JellySyncPayload(snapshot));
            }
        }
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        ACTIVE.clear();
        LAST_SENT.clear();
    }

    private static boolean isPlayingOurDisc(ServerLevel level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        return level.getBlockEntity(pos) instanceof JukeboxBlockEntity jukebox
                && jukebox.getSongPlayer().isPlaying()
                && jukebox.getTheItem().is(ModItems.JELLY_DISC.get());
    }

    private JellyJukeboxTracker() {}
}