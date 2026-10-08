package com.bydalc;

import com.bydalc.event.JellyJukeboxTracker;
import com.bydalc.network.JellyNetwork;
import com.bydalc.registry.ModItems;
import com.bydalc.registry.ModSounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BydalcMod.MODID)
public final class BydalcMod {
    public static final String MODID = "bydalc";

    public BydalcMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, JellyConfig.SPEC);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(JellyConfig::onConfigChanged);

        ModSounds.SOUNDS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        JellyNetwork.register();
        modEventBus.addListener(ModItems::addToCreativeTab);

        IEventBus gameBus = MinecraftForge.EVENT_BUS;
        gameBus.addListener(JellyJukeboxTracker::onGameEvent);
        gameBus.addListener(JellyJukeboxTracker::onServerTick);
        gameBus.addListener(JellyJukeboxTracker::onServerStopped);
    }
}
