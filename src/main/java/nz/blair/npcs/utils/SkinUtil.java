package nz.blair.npcs.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import nz.blair.npcs.npcs.Skin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Utility class for getting skins.
 */
@SuppressWarnings("unused") // This class is used by other plugins
public class SkinUtil {
    /**
     * Get the skin of a player
     *
     * @param player The player to get the skin of
     * @return The skin of the player
     */
    public static Skin getSkin(Player player) {
        Property property = NmsUtil.getGameProfile(player).getProperties().get("textures").iterator().next();
        return new Skin(property.getValue(), property.getSignature());
    }

    /**
     * Get the skin of a player
     * This method is blocking and should not be called on the main thread
     *
     * @param username The username of the player to get the skin of
     * @return The skin of the player
     */
    @Nullable
    public static Skin getSkin(String username) {
        UUID uuid = getUuid(username);
        if (uuid == null) {
            return null;
        }

        return getSkin(uuid);
    }

    /**
     * Get the skin of a player
     *
     * @param uuid The UUID of the player to get the skin of
     * @return The skin of the player
     */
    @Nullable
    public static Skin getSkin(UUID uuid) {
        try {
            String profileString = HttpUtil.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString() + "?unsigned=false");
            if (profileString == null) {
                return null;
            }

            JsonObject profileJson = new JsonParser().parse(profileString).getAsJsonObject();
            JsonObject propertiesJson = profileJson.getAsJsonArray("properties").get(0).getAsJsonObject();
            String value = propertiesJson.get("value").getAsString();
            String signature = propertiesJson.get("signature").getAsString();
            return new Skin(value, signature);
        } catch (Exception e) {
            LoggerUtil.warning("Failed to get skin of " + uuid.toString(), e);
        }

        return null;
    }

    @Nullable
    private static UUID getUuid(String username) {
        try {
            String profileString = HttpUtil.get("https://api.mojang.com/users/profiles/minecraft/" + username);
            if (profileString == null) {
                return null;
            }
            JsonObject profileJson = new JsonParser().parse(profileString).getAsJsonObject();
            String uuidString = profileJson.get("id").getAsString();
            return UUID.fromString(uuidString);
        } catch (Exception e) {
            LoggerUtil.warning("Failed to get UUID of " + username, e);
        }

        return null;
    }
}
