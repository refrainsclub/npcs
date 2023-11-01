package nz.blair.npcs.npcs;

import java.util.*;

public class NpcManager {
    private final Set<Npc> npcs = new HashSet<>();

    /**
     * Add an NPC to the manager.
     *
     * @param npc The NPC to add
     * @return Whether the NPC was added
     */
    @SuppressWarnings("UnusedReturnValue") // This return value could be useful in the future
    public boolean addNpc(Npc npc) {
        return npcs.add(npc);
    }

    /**
     * Remove an NPC from the manager.
     *
     * @param npc The NPC to remove
     * @return Whether the NPC was removed
     */
    public boolean removeNpc(Npc npc) {
        return npcs.remove(npc);
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
