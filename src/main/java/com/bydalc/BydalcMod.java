package com.bydalc;

import com.bydalc.event.JellyJukeboxTracker;
import com.bydalc.network.JellyNetwork;
import com.bydalc.registry.ModItems;
import com.bydalc.registry.ModSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(BydalcMod.MODID)
public final class BydalcMod {
    public static final String MODID = "bydalc";

    public BydalcMod(IEventBus modEventBus) {
        ModSounds.SOUNDS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(JellyNetwork::register);
        modEventBus.addListener(ModItems::addToCreativeTab);

        IEventBus gameBus = NeoForge.EVENT_BUS;
        gameBus.addListener(JellyJukeboxTracker::onGameEvent);
        gameBus.addListener(JellyJukeboxTracker::onServerTick);
        gameBus.addListener(JellyJukeboxTracker::onServerStopped);

    }
}