package nz.blair.npcs.npcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NpcManager {
    private final List<Npc> npcs = new ArrayList<>();

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
     * Get a list of all NPCs.
     *
     * @return A list of all NPCs
     */
    public List<Npc> getNpcs() {
        return Collections.unmodifiableList(npcs);
    }
}
