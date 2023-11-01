package nz.blair.npcs.npcs;

import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.jetbrains.annotations.Nullable;

public enum ClickType {
    LEFT_CLICK, RIGHT_CLICK;

    @Nullable
    public static ClickType fromNms(PacketPlayInUseEntity.EnumEntityUseAction action) {
        switch (action) {
            case ATTACK:
                return LEFT_CLICK;
            case INTERACT_AT:
                return RIGHT_CLICK;
            default:
                return null;
        }
    }
}
