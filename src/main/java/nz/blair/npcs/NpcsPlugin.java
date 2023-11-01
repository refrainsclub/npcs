package nz.blair.npcs;

import nz.blair.npcs.listeners.PacketInboundListener;
import nz.blair.npcs.listeners.PlayerListener;
import nz.blair.npcs.npcs.Npc;
import nz.blair.npcs.npcs.NpcFactory;
import nz.blair.npcs.npcs.NpcManager;
import nz.blair.npcs.packets.PacketHandlerInjector;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main plugin class.
 * This is the entry point of the plugin.
 * This class is responsible for creating the API singleton and registering events.
 */
@SuppressWarnings("unused") // This class is used by other plugins
public final class NpcsPlugin extends JavaPlugin {
    private static NpcsApi npcsApi;
    private NpcManager npcManager;

    @Override
    public void onEnable() {
        npcManager = new NpcManager();
        NpcFactory npcFactory = new NpcFactory(this);
        npcsApi = new NpcsApi(npcManager, npcFactory);

        PacketInboundListener packetInboundListener = new PacketInboundListener(npcManager);
        PacketHandlerInjector packetHandlerInjector = new PacketHandlerInjector(packetInboundListener);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerListener(packetHandlerInjector, npcManager, this), this);
    }

    @Override
    public void onDisable() {
        npcManager.getNpcs().forEach(Npc::removeConnections);
    }

    /**
     * Get the API singleton.
     *
     * @return The API singleton
     */
    public static NpcsApi getApi() {
        return npcsApi;
    }
}
