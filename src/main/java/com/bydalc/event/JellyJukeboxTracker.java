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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.network.PacketDistributor;

/**
 * Keeps track of the jukeboxes that hold the jelly disc and forwards their positions to every
 * client in the dimension, so the wobble can be rendered there.
 *
 * <p>跟踪条件是「唱片还在唱片机里」而不是「正在播放」：原版唱片放完一次就会停，这里会在发现
 * 播放中断时立刻重新开始，实现循环播放。顺带也能从任何一次意外中断里自动恢复。
 */
public final class JellyJukeboxTracker {
    private static final Map<ResourceKey<Level>, Set<BlockPos>> ACTIVE = new HashMap<>();
    private static final Map<ResourceKey<Level>, List<BlockPos>> LAST_SENT = new HashMap<>();

    public static void onGameEvent(VanillaGameEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        // BLOCK_CHANGE 用于在唱片刚塞进去时立刻发现唱片机；
        // JUKEBOX_PLAY 是播放期间每 20 tick 一次的心跳，作为兜底。
        GameEvent gameEvent = event.getVanillaEvent();
        if (gameEvent != GameEvent.JUKEBOX_PLAY && gameEvent != GameEvent.BLOCK_CHANGE) {
            return;
        }

        BlockPos pos = BlockPos.containing(event.getEventPosition());
        if (containsOurDisc(level, pos)) {
            ACTIVE.computeIfAbsent(level.dimension(), key -> new HashSet<>()).add(pos);
        }
    }

    public static void onServerTick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = event.getServer();
        boolean heartbeat = server.getTickCount() % 20 == 0;

        for (ServerLevel level : server.getAllLevels()) {
            ResourceKey<Level> key = level.dimension();
            Set<BlockPos> active = ACTIVE.get(key);

            if (active != null) {
                // 唱片被取走、或方块被破坏，就不再跟踪。
                active.removeIf(pos -> !containsOurDisc(level, pos));

                // 循环播放：只要唱片还在里面，放完（或被任何原因打断）就从头重来。
                for (BlockPos pos : active) {
                    if (level.getBlockEntity(pos) instanceof JukeboxBlockEntity jukebox
                            && !jukebox.isRecordPlaying()) {
                        jukebox.startPlaying();
                    }
                }

                if (active.isEmpty()) {
                    ACTIVE.remove(key);
                    active = null;
                }
            }

            List<BlockPos> snapshot = active == null ? List.of() : new ArrayList<>(active);
            boolean changed = !snapshot.equals(LAST_SENT.get(key));
            if (changed || (heartbeat && !snapshot.isEmpty())) {
                LAST_SENT.put(key, snapshot);
                JellyNetwork.CHANNEL.send(
                        PacketDistributor.DIMENSION.with(level::dimension),
                        new JellyNetwork.JellySyncMessage(snapshot));
            }
        }
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        ACTIVE.clear();
        LAST_SENT.clear();
    }

    private static boolean containsOurDisc(ServerLevel level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        return level.getBlockEntity(pos) instanceof JukeboxBlockEntity jukebox
                && jukebox.getFirstItem().is(ModItems.JELLY_DISC.get());
    }

    private JellyJukeboxTracker() {}
}
