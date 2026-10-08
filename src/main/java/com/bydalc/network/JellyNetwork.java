package com.bydalc.network;

import com.bydalc.BydalcMod;
import com.bydalc.JellyClientState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * 1.20.1 Forge 使用 SimpleChannel 收发自定义包：服务端把「正在播放唱片的唱片机坐标」下发给
 * 同一维度的所有客户端，客户端据此决定哪些实体要做果冻形变。
 */
public final class JellyNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BydalcMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void register() {
        CHANNEL.registerMessage(
                0,
                JellySyncMessage.class,
                JellySyncMessage::encode,
                JellySyncMessage::decode,
                JellySyncMessage::handle);
    }

    private JellyNetwork() {}

    public static final class JellySyncMessage {
        private final List<BlockPos> positions;

        public JellySyncMessage(List<BlockPos> positions) {
            this.positions = positions;
        }

        public static void encode(JellySyncMessage message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.positions.size());
            for (BlockPos pos : message.positions) {
                buffer.writeBlockPos(pos);
            }
        }

        public static JellySyncMessage decode(FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            List<BlockPos> positions = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                positions.add(buffer.readBlockPos());
            }
            return new JellySyncMessage(positions);
        }

        public static void handle(JellySyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> JellyClientState.accept(message.positions));
            context.setPacketHandled(true);
        }
    }
}
