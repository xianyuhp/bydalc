package com.bydalc.registry;

import com.bydalc.BydalcMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BydalcMod.MODID);

    /**
     * 唱片时长（秒）。{@link RecordItem} 的第 4 个参数单位是秒，它内部会再乘 20 转成 tick，
     * 所以这里必须填秒数。唱片机据此判断何时停止播放。
     */
    private static final int DISC_LENGTH_SECONDS = 191;

    public static final RegistryObject<Item> JELLY_DISC = ITEMS.register(
            "bydj",
            () -> new RecordItem(
                    15,
                    ModSounds.MUSIC_DISC_JELLY.get(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE),
                    DISC_LENGTH_SECONDS));

    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(JELLY_DISC);
        }
    }

    private ModItems() {}
}
