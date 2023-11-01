package nz.blair.npcs.packets;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.server.v1_8_R3.Packet;
import nz.blair.npcs.listeners.PacketInboundListener;
import org.bukkit.entity.Player;

public class PacketHandler extends ChannelDuplexHandler {
    private final Player player;
    private final PacketInboundListener packetInboundListener;

    public PacketHandler(Player player, PacketInboundListener packetInboundListener) {
        this.player = player;
        this.packetInboundListener = packetInboundListener;
    }

    @Override
    public void channelRead(ChannelHandlerContext channel, Object packet) throws Exception {
        boolean cancelled = packetInboundListener.onPacketInbound(player, (Packet<?>) packet);

        if (!cancelled) {
            super.channelRead(channel, packet);
        }
    }
}
