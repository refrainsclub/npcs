package nz.blair.npcs.listeners;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import nz.blair.npcs.npcs.ClickAction;
import nz.blair.npcs.npcs.ClickType;
import nz.blair.npcs.npcs.NpcManager;
import nz.blair.npcs.utils.NmsUtil;
import org.bukkit.entity.Player;

public class PacketInboundListener {
    private final NpcManager npcManager;

    public PacketInboundListener(NpcManager npcManager) {
        this.npcManager = npcManager;
    }

    @SuppressWarnings("SameReturnValue") // Keep the cancel return value for future use
    public boolean onPacketInbound(Player player, Packet<?> packet) {
        if (!(packet instanceof PacketPlayInUseEntity)) {
            return false;
        }

        PacketPlayInUseEntity useEntityPacket = (PacketPlayInUseEntity) packet;
        PacketPlayInUseEntity.EnumEntityUseAction action = useEntityPacket.a();
        Object entityIdObj = NmsUtil.getField(useEntityPacket, "a");

        if (!(entityIdObj instanceof Integer)) {
            return false;
        }

        int entityId = (int) entityIdObj;

        npcManager.getNpcs().forEach(npc -> {
            if (npc.getEntityId() == entityId) {
                ClickAction clickAction = npc.getClickAction();

                if (clickAction == null) {
                    return;
                }

                ClickType clickType = ClickType.fromNms(action);

                if (clickType == null) {
                    return;
                }

                clickAction.onClick(player, clickType);
            }
        });

        return false;
    }
}
