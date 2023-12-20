package nz.blair.npcs.utils;

import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class NmsUtil {
    public static CraftPlayer getCraftPlayer(Player player) {
        return (CraftPlayer) player;
    }

    public static EntityPlayer getEntityPlayer(Player player) {
        return getCraftPlayer(player).getHandle();
    }

    public static PlayerConnection getPlayerConnection(Player player) {
        return getEntityPlayer(player).playerConnection;
    }

    public static ChannelPipeline getChannelPipeline(Player player) {
        PlayerConnection connection = getPlayerConnection(player);
        return connection.networkManager.channel.pipeline();
    }

    public static GameProfile getGameProfile(Player player) {
        EntityPlayer entityPlayer = getEntityPlayer(player);
        return entityPlayer.getProfile();
    }

    public static CraftWorld getCraftWorld(World world) {
        return (CraftWorld) world;
    }

    public static WorldServer getWorldServer(World world) {
        return getCraftWorld(world).getHandle();
    }

    public static MinecraftServer getMinecraftServer() {
        return MinecraftServer.getServer();
    }

    @Nullable
    public static Object getField(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LoggerUtil.warning("Failed to get field: " + name + " from object: " + object.getClass().getSimpleName(), e);
            return null;
        }
    }

    public static void setField(Object object, String name, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LoggerUtil.warning("Failed to set field: " + name + " from object: " + object.getClass().getSimpleName(), e);
        }
    }
}
