package com.bydalc.network;

import com.bydalc.JellyClientState;
import com.bydalc.BydalcMod;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class JellyNetwork {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(JellySyncPayload.TYPE, JellySyncPayload.STREAM_CODEC, JellyNetwork::onSync);
    }

    private static void onSync(JellySyncPayload payload, IPayloadContext context) {
        JellyClientState.accept(payload.positions());
    }

    private JellyNetwork() {}

    public record JellySyncPayload(List<BlockPos> positions) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<JellySyncPayload> TYPE = new CustomPacketPayload.Type<>(
                ResourceLocation.fromNamespaceAndPath(BydalcMod.MODID, "jukebox_sync"));

        public static final StreamCodec<RegistryFriendlyByteBuf, JellySyncPayload> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
                JellySyncPayload::positions,
                JellySyncPayload::new);

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}