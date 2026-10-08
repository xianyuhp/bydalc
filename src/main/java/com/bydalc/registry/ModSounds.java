package com.bydalc.registry;

import com.bydalc.BydalcMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BydalcMod.MODID);

    public static final RegistryObject<SoundEvent> MUSIC_DISC_JELLY = SOUNDS.register(
            "bangyou_de_jiu",
            () -> SoundEvent.createVariableRangeEvent(
                    new ResourceLocation(BydalcMod.MODID, "bangyou_de_jiu")));

    private ModSounds() {}
}
