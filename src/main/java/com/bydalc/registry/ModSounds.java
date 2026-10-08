package com.bydalc.registry;

import com.bydalc.BydalcMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, BydalcMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_JELLY = SOUNDS.register(
            "bangyou_de_jiu",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(BydalcMod.MODID, "bangyou_de_jiu")));

    private ModSounds() {}
}