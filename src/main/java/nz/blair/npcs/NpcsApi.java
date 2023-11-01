package nz.blair.npcs;

import nz.blair.npcs.npcs.Npc;
import nz.blair.npcs.npcs.NpcFactory;
import nz.blair.npcs.npcs.NpcManager;
import org.bukkit.Location;

import java.util.List;

/**
 * The API for the NPCs plugin.
 * This is the main class that you will use to interact with the plugin.
 */
@SuppressWarnings("unused") // This class is used by other plugins
public class NpcsApi {
    private final NpcManager npcManager;
    private final NpcFactory npcFactory;

    /**
     * Create a new instance of the API.
     * Do not use this constructor, instead use {@link nz.blair.npcs.NpcsPlugin#getApi()}.
     *
     * @param npcManager The NPC manager
     * @param npcFactory The NPC factory
     */
    public NpcsApi(NpcManager npcManager, NpcFactory npcFactory) {
        this.npcManager = npcManager;
        this.npcFactory = npcFactory;
    }

    /**
     * Create a new NPC with the given name and location.
     * This will automatically add the NPC to the NPC manager.
     *
     * @param name     The name of the NPC
     * @param location The location of the NPC
     * @param global   Whether the NPC should be visible to all players
     * @return The NPC
     */
    public Npc createNpc(String name, Location location, boolean global) {
        Npc npc = npcFactory.createNpc(name, location, global);
        npcManager.addNpc(npc);

        return npc;
    }

    /**
     * Delete an NPC.
     * This will automatically remove the NPC from the NPC manager.
     *
     * @param npc The NPC to delete
     */
    public void deleteNpc(Npc npc) {
        npc.removeConnections();
        npcManager.removeNpc(npc);
    }

    /**
     * Get a list of all NPCs.
     *
     * @return A list of all NPCs
     */
    public List<Npc> getNpcs() {
        return npcManager.getNpcs();
    }
}
