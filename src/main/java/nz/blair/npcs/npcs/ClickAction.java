package nz.blair.npcs.npcs;

import org.bukkit.entity.Player;

public interface ClickAction {
    void onClick(Player player, ClickType clickType);
}
