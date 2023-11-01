package nz.blair.npcs.listeners;

import net.minecraft.server.v1_8_R3.PlayerConnection;
import nz.blair.npcs.npcs.NpcManager;
import nz.blair.npcs.packets.PacketHandlerInjector;
import nz.blair.npcs.utils.NmsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {
    private final PacketHandlerInjector packetHandlerInjector;
    private final NpcManager npcManager;
    private final JavaPlugin plugin;

    public PlayerListener(PacketHandlerInjector packetHandlerInjector, NpcManager npcManager, JavaPlugin plugin) {
        this.packetHandlerInjector = packetHandlerInjector;
        this.npcManager = npcManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        packetHandlerInjector.inject(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerConnection connection = NmsUtil.getPlayerConnection(player);

        npcManager.getNpcs().forEach(npc -> {
            // This will also remove the connection from the NPC
            npc.removeAllowedConnection(connection);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        manageNpcsInRange(player, event.getTo());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        manageNpcsInRange(player, event.getTo());
    }

    private void manageNpcsInRange(Player player, Location location) {
        // Run this task asynchronously to avoid blocking the main thread
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerConnection connection = NmsUtil.getPlayerConnection(player);
            npcManager.getNpcs().forEach(npc -> npc.manageInRange(connection, location));
        });
    }
}
