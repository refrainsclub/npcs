package nz.blair.npcs.npcs;

import java.util.*;

public class NpcManager {
    private final Set<Npc> npcs = new HashSet<>();

    /**
     * Add an NPC to the manager.
     *
     * @param npc The NPC to add
     */
    public void addNpc(Npc npc) {
        npcs.add(npc);
    }

    /**
     * Remove an NPC from the manager.
     *
     * @param npc The NPC to remove
     */
    public void removeNpc(Npc npc) {
        npcs.remove(npc);
    }

    /**
     * Get a set of all NPCs.
     *
     * @return A set of all NPCs
     */
    public Set<Npc> getNpcs() {
        return Collections.unmodifiableSet(npcs);
    }
}
