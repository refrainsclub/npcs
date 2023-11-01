package nz.blair.npcs.npcs;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class NpcFactory {
    private final JavaPlugin plugin;

    public NpcFactory(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a new NPC with the given name and location.
     * Make sure to add the NPC to the NPC manager.
     * This is done automatically by the {@link nz.blair.npcs.NpcsApi}.
     *
     * @param name     The name of the NPC
     * @param location The location of the NPC
     * @param global   Whether the NPC should be visible to all players
     * @return The NPC
     */
    public Npc createNpc(String name, Location location, boolean global) {
        return new Npc(name, location, global, plugin);
    }
}
