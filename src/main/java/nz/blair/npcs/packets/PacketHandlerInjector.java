package nz.blair.npcs.packets;

import io.netty.channel.ChannelPipeline;
import nz.blair.npcs.listeners.PacketInboundListener;
import nz.blair.npcs.utils.NmsUtil;
import org.bukkit.entity.Player;

public class PacketHandlerInjector {
    private static final String PACKET_HANDLER_NAME = "npcs";
    private final PacketInboundListener packetInboundListener;

    public PacketHandlerInjector(PacketInboundListener packetInboundListener) {
        this.packetInboundListener = packetInboundListener;
    }

    public void inject(Player player) {
        ChannelPipeline pipeline = NmsUtil.getChannelPipeline(player);
        pipeline.addAfter("decoder", PACKET_HANDLER_NAME, new PacketHandler(player, packetInboundListener));
    }
}
