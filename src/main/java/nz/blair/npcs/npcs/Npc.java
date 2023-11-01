package nz.blair.npcs.npcs;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import nz.blair.npcs.utils.NmsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents an NPC.
 * You should use the {@link nz.blair.npcs.NpcsApi} singleton to create NPCs.
 * You can get this singleton by calling {@link nz.blair.npcs.NpcsPlugin#getApi()}.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"}) // This class is used by other plugins
public class Npc {
    private final JavaPlugin plugin;
    private final EntityPlayer entityPlayer;
    private final Set<PlayerConnection> connections = new HashSet<>();
    @Nullable
    private ClickAction clickAction = null;

    // It is easier to manage armour here than use the EntityPlayer class
    @Nullable
    private ItemStack helmet = null;
    @Nullable
    private ItemStack chestplate = null;
    @Nullable
    private ItemStack leggings = null;
    @Nullable
    private ItemStack boots = null;

    // If this is true, the npc will be shown to everyone.
    // Otherwise, the npc will be shown to connections in the allowed connections set.
    private boolean global;

    // The connections that are allowed to see the npc.
    // Only applies if global is false.
    private final Set<PlayerConnection> allowedConnections = new HashSet<>();

    /**
     * Creates a new NPC.
     * You should use the {@link nz.blair.npcs.NpcsApi} singleton to create NPCs.
     * You can get this singleton by calling {@link nz.blair.npcs.NpcsPlugin#getApi()}.
     *
     * @param name     The name of the NPC
     * @param location The location of the NPC
     * @param global   Whether the NPC should be shown to everyone
     * @param plugin   The NpcsPlugin instance
     */
    public Npc(String name, Location location, boolean global, JavaPlugin plugin) {
        this.plugin = plugin;
        this.global = global;

        MinecraftServer minecraftServer = NmsUtil.getMinecraftServer();
        World world = location.getWorld();
        WorldServer worldServer = NmsUtil.getWorldServer(world);
        UUID uuid = UUID.randomUUID();
        GameProfile gameProfile = new GameProfile(uuid, name);
        PlayerInteractManager playerInteractManager = new PlayerInteractManager(worldServer);
        entityPlayer = new EntityPlayer(minecraftServer, worldServer, gameProfile, playerInteractManager);

        setLocation(location);

        // Enable skin outer layer
        entityPlayer.getDataWatcher().watch(10, (byte) 127);
    }

    /**
     * Adds a connection to the NPC.
     * You should not need to call this method.
     * This method is used internally.
     * It also checks if the connection is allowed to see the NPC.
     * This will be cleaned up automatically when the connection disconnects.
     *
     * @param connection The connection to add
     * @return Whether the connection was added
     */
    public boolean addConnection(PlayerConnection connection) {
        if (!global && !allowedConnections.contains(connection)) {
            return false;
        }

        boolean added = connections.add(connection);

        if (added) {
            spawn(connection);
        }

        return added;
    }

    /**
     * Removes a connection from the NPC.
     * You should not need to call this method.
     * This method is used internally.
     * This will be cleaned up automatically when the connection disconnects.
     *
     * @param connection The connection to remove
     * @return Whether the connection was removed
     */
    public boolean removeConnection(PlayerConnection connection) {
        boolean removed = connections.remove(connection);

        if (removed) {
            destroy(connection);
        }

        return removed;
    }

    /**
     * Adds multiple connections to the NPC.
     * It also checks if each connection is allowed to see the NPC.
     *
     * @param connections The connections to add
     */
    public void addConnections(Collection<PlayerConnection> connections) {
        connections.forEach(this::addConnection);
    }

    /**
     * Removes multiple connections from the NPC.
     *
     * @param connections The connections to remove
     */
    public void removeConnections(Collection<PlayerConnection> connections) {
        connections.forEach(this::destroy);
        connections.clear();
    }

    /**
     * Removes all connections from the NPC.
     * You should not need to call this method.
     * This method is used internally.
     */
    public void removeConnections() {
        connections.forEach(this::destroy);
        connections.clear();
    }

    /**
     * Adds a connection to the allowed connections set.
     * If the NPC is not global, only connections in this set will be able to see the NPC.
     * This will be cleaned up automatically when the connection disconnects.
     *
     * @param connection The connection to add
     * @return Whether the connection was added
     */
    public boolean addAllowedConnection(PlayerConnection connection) {
        boolean added = allowedConnections.add(connection);

        if (added) {
            // Add connection based on whether the connection is in range
            manageInRange(connection, connection.getPlayer().getLocation());
        }

        return added;
    }

    /**
     * Removes a connection from the allowed connections set.
     * If the NPC is not global, only connections in this set will be able to see the NPC.
     * This will be cleaned up automatically when the connection disconnects.
     * This will also remove the connection from the connections set.
     *
     * @param connection The connection to remove
     * @return Whether the connection was removed
     */
    public boolean removeAllowedConnection(PlayerConnection connection) {
        boolean removed = allowedConnections.remove(connection);

        if (removed) {
            // Use this method so that it will destroy the npc for the connection
            removeConnection(connection);
        }

        return removed;
    }

    /**
     * Adds multiple connections to the allowed connections set.
     * If the NPC is not global, only connections in this set will be able to see the NPC.
     * This will be cleaned up automatically when the connection disconnects.
     *
     * @param connections The connections to add
     */
    public void addAllowedConnections(Collection<PlayerConnection> connections) {
        connections.forEach(this::addAllowedConnection);
    }

    /**
     * Removes multiple connections from the allowed connections set.
     * If the NPC is not global, only connections in this set will be able to see the NPC.
     * This will be cleaned up automatically when the connection disconnects.
     *
     * @param connections The connections to remove
     */
    public void removeAllowedConnections(Collection<PlayerConnection> connections) {
        connections.forEach(this::removeAllowedConnection);
    }

    /**
     * Removes all connections from the allowed connections set.
     * If the NPC is not global, only connections in this set will be able to see the NPC.
     * This will be cleaned up automatically when the connection disconnects.
     * This will also remove all connections from the connections set.
     */
    public void removeAllowedConnections() {
        allowedConnections.forEach(this::removeConnection);
        allowedConnections.clear();
    }

    /**
     * Gets all connections of the NPC.
     *
     * @return The connections of the NPC
     */
    public Set<PlayerConnection> getConnections() {
        return Collections.unmodifiableSet(connections);
    }

    /**
     * Gets all allowed connections of the NPC.
     * If the NPC is not global, only connections in this set will be able to see the NPC.
     *
     * @return The allowed connections of the NPC
     */
    public Set<PlayerConnection> getAllowedConnections() {
        return Collections.unmodifiableSet(allowedConnections);
    }

    /**
     * Checks if the NPC has the given connection in the connections set.
     *
     * @param connection The connection to check
     * @return Whether the NPC has the connection in the connections set
     */
    public boolean hasConnection(PlayerConnection connection) {
        return connections.contains(connection);
    }

    /**
     * Checks if the NPC has the given connection in the allowed connections set.
     * If the NPC is not global, only connections in this set will be able to see the NPC.
     *
     * @param connection The connection to check
     * @return Whether the NPC has the connection in the allowed connections set
     */
    public boolean hasAllowedConnection(PlayerConnection connection) {
        return allowedConnections.contains(connection);
    }

    /**
     * Checks if the NPC is global.
     * If the NPC is global, it will be shown to everyone.
     * If the NPC is not global, it will only be shown to connections in the allowed connections set.
     *
     * @return Whether the NPC is global
     */
    public boolean isGlobal() {
        return global;
    }

    /**
     * Sets whether the NPC is global.
     * If the NPC is global, it will be shown to everyone.
     * If the NPC is not global, it will only be shown to connections in the allowed connections set.
     *
     * @param global Whether the NPC is global
     */
    public void setGlobal(boolean global) {
        this.global = global;

        if (!global) {
            connections.forEach(connection -> {
                if (!allowedConnections.contains(connection)) {
                    removeConnection(connection);
                }
            });

            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerConnection connection = NmsUtil.getPlayerConnection(player);
            manageInRange(connection, player.getLocation());
        });
    }

    private void spawn(PlayerConnection connection) {
        PacketPlayOutPlayerInfo addPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(entityPlayer);
        PacketPlayOutPlayerInfo removePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);

        connection.sendPacket(addPacket);
        connection.sendPacket(spawnPacket);

        updateLocation(connection);
        updateArmour(connection);

        // This will remove the name from the player list after 8 seconds
        // The NPC will still be visible
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> connection.sendPacket(removePacket), 8 * 20);
    }

    private void destroy(PlayerConnection connection) {
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityPlayer.getId());
        connection.sendPacket(destroyPacket);
    }

    private void respawn() {
        connections.forEach(this::destroy);
        connections.forEach(this::spawn);
    }

    /**
     * Sets the location of the NPC.
     *
     * @param location The location to set
     */
    public void setLocation(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        entityPlayer.setLocation(x, y, z, yaw, pitch);
        updateLocation();
    }

    private void updateLocation(PlayerConnection connection) {
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entityPlayer.getId(), MathHelper.floor(entityPlayer.locX * 32), MathHelper.floor(entityPlayer.locY * 32), MathHelper.floor(entityPlayer.locZ * 32), (byte) ((int) (entityPlayer.yaw * 256 / 360)), (byte) ((int) (entityPlayer.pitch * 256 / 360)), true);
        PacketPlayOutEntity.PacketPlayOutEntityLook lookPacket = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(), (byte) ((int) (entityPlayer.yaw * 256F / 360F)), (byte) ((int) (entityPlayer.pitch * 256F / 360F)), true);
        PacketPlayOutEntityHeadRotation headRotationPacket = new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) ((int) (entityPlayer.yaw * 256F / 360F)));

        connection.sendPacket(teleportPacket);
        connection.sendPacket(lookPacket);
        connection.sendPacket(headRotationPacket);
    }

    private void updateLocation() {
        connections.forEach(this::updateLocation);
    }

    /**
     * Gets the location of the NPC.
     *
     * @return The location of the NPC
     */
    public Location getLocation() {
        World world = entityPlayer.world.getWorld();
        double x = entityPlayer.locX;
        double y = entityPlayer.locY;
        double z = entityPlayer.locZ;
        float yaw = entityPlayer.yaw;
        float pitch = entityPlayer.pitch;

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Sets the skin of the NPC.
     * You can get a skin using the {@link nz.blair.npcs.utils.SkinUtil} class.
     *
     * @param skin The skin to set
     */
    public void setSkin(@Nullable Skin skin) {
        GameProfile gameProfile = entityPlayer.getProfile();
        gameProfile.getProperties().removeAll("textures");
        if (skin != null) {
            Property property = new Property("textures", skin.getValue(), skin.getSignature());
            gameProfile.getProperties().put("textures", property);
        }

        respawn();
    }

    /**
     * Gets the skin of the NPC.
     *
     * @return The skin of the NPC
     */
    @Nullable
    public Skin getSkin() {
        GameProfile gameProfile = entityPlayer.getProfile();

        Iterator<Property> iterator = gameProfile.getProperties().get("textures").iterator();
        if (iterator.hasNext()) {
            Property property = iterator.next();
            return new Skin(property.getValue(), property.getSignature());
        }

        return null;
    }

    /**
     * Sets the click action of the NPC.
     * This action will be called when a player clicks on the NPC.
     * It will be called asynchronously.
     *
     * @param clickAction The click action to set
     */
    public void setClickAction(@Nullable ClickAction clickAction) {
        this.clickAction = clickAction;
    }

    /**
     * Gets the click action of the NPC.
     * This action will be called when a player clicks on the NPC.
     * It will be called asynchronously.
     *
     * @return The click action of the NPC
     */
    @Nullable
    public ClickAction getClickAction() {
        return clickAction;
    }

    /**
     * Sets the item in the hand of the NPC.
     *
     * @param item The item to set
     */
    public void setItemInHand(ItemStack item) {
        entityPlayer.getBukkitEntity().setItemInHand(item);

        PacketPlayOutEntityEquipment equipmentPacket = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 0, CraftItemStack.asNMSCopy(item));
        connections.forEach(connection -> connection.sendPacket(equipmentPacket));
    }

    /**
     * Gets the item in the hand of the NPC.
     *
     * @return The item in the hand of the NPC
     */
    public ItemStack getItemInHand() {
        return entityPlayer.getBukkitEntity().getItemInHand();
    }

    /**
     * Sets the helmet of the NPC.
     *
     * @param item The helmet to set
     */
    public void setHelmet(@Nullable ItemStack item) {
        helmet = item;
        updateHelmet();
    }

    private void updateHelmet(PlayerConnection connection) {
        PacketPlayOutEntityEquipment equipmentPacket = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 1, CraftItemStack.asNMSCopy(helmet));
        connection.sendPacket(equipmentPacket);
    }

    private void updateHelmet() {
        connections.forEach(this::updateHelmet);
    }

    /**
     * Gets the helmet of the NPC.
     *
     * @return The helmet of the NPC
     */
    @Nullable
    public ItemStack getHelmet() {
        return helmet;
    }

    /**
     * Sets the chestplate of the NPC.
     *
     * @param item The chestplate to set
     */
    public void setChestplate(@Nullable ItemStack item) {
        chestplate = item;
        updateChestplate();
    }

    private void updateChestplate(PlayerConnection connection) {
        PacketPlayOutEntityEquipment equipmentPacket = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 2, CraftItemStack.asNMSCopy(chestplate));
        connection.sendPacket(equipmentPacket);
    }

    private void updateChestplate() {
        connections.forEach(this::updateChestplate);
    }

    /**
     * Gets the chestplate of the NPC.
     *
     * @return The chestplate of the NPC
     */
    @Nullable
    public ItemStack getChestplate() {
        return chestplate;
    }

    /**
     * Sets the leggings of the NPC.
     *
     * @param item The leggings to set
     */
    public void setLeggings(@Nullable ItemStack item) {
        leggings = item;
        updateLeggings();
    }

    private void updateLeggings(PlayerConnection connection) {
        PacketPlayOutEntityEquipment equipmentPacket = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 3, CraftItemStack.asNMSCopy(leggings));
        connection.sendPacket(equipmentPacket);
    }

    private void updateLeggings() {
        connections.forEach(this::updateLeggings);
    }

    /**
     * Gets the leggings of the NPC.
     *
     * @return The leggings of the NPC
     */
    @Nullable
    public ItemStack getLeggings() {
        return leggings;
    }

    /**
     * Sets the boots of the NPC.
     *
     * @param item The boots to set
     */
    public void setBoots(@Nullable ItemStack item) {
        this.boots = item;
        updateBoots();
    }

    private void updateBoots(PlayerConnection connection) {
        PacketPlayOutEntityEquipment equipmentPacket = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 4, CraftItemStack.asNMSCopy(boots));
        connection.sendPacket(equipmentPacket);
    }

    private void updateBoots() {
        connections.forEach(this::updateBoots);
    }

    /**
     * Gets the boots of the NPC.
     *
     * @return The boots of the NPC
     */
    @Nullable
    public ItemStack getBoots() {
        return boots;
    }

    private void updateArmour(PlayerConnection connection) {
        updateHelmet(connection);
        updateChestplate(connection);
        updateLeggings(connection);
        updateBoots(connection);
    }

    private void updateArmour() {
        updateHelmet();
        updateChestplate();
        updateLeggings();
        updateBoots();
    }

    /**
     * Plays an animation for the NPC.
     *
     * @param animation The animation to play
     */
    public void playAnimation(Animation animation) {
        PacketPlayOutAnimation animationPacket = new PacketPlayOutAnimation(entityPlayer, animation.getId());
        connections.forEach(connection -> connection.sendPacket(animationPacket));
    }

    /**
     * Sets the sneak state of the NPC.
     *
     * @param sneaking The sneak state to set
     */
    public void setSneaking(boolean sneaking) {
        entityPlayer.setSneaking(sneaking);
        connections.forEach(connection -> connection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), entityPlayer.getDataWatcher(), true)));
    }

    /**
     * Gets the sneak state of the NPC.
     *
     * @return The sneak state of the NPC
     */
    public boolean isSneaking() {
        return entityPlayer.isSneaking();
    }

    /**
     * Gets the name of the NPC.
     *
     * @return The name of the NPC
     */
    public String getName() {
        return entityPlayer.getName();
    }

    /**
     * Gets the ID of the NPC.
     * You should not need to call this method.
     * This method is used internally.
     *
     * @return The ID of the NPC
     */
    public int getEntityId() {
        return entityPlayer.getId();
    }

    /**
     * Spawns the NPC for the given connection if the connection is in range.
     * Otherwise, the NPC will be removed from the connection.
     * You should not need to call this method.
     * This method is used internally.
     *
     * @param connection The connection to manage
     * @param location   The location to check
     */
    public boolean manageInRange(PlayerConnection connection, Location location) {
        World world = connection.getPlayer().getWorld();
        Location npcLocation = getLocation();
        World npcWorld = npcLocation.getWorld();
        boolean sameWorld = world.equals(npcWorld);

        if (!sameWorld) {
            removeConnection(connection);
            return false;
        }

        double distanceSquared = npcLocation.distanceSquared(location);
        boolean inRange = distanceSquared <= 1024;

        if (inRange) {
            addConnection(connection);
        } else {
            removeConnection(connection);
        }

        return inRange;
    }
}
