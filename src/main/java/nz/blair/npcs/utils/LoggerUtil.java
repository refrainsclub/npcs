package nz.blair.npcs.utils;

import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil {
    private static final Logger logger = Bukkit.getLogger();

    public static void warning(String message, Throwable throwable) {
        logger.log(Level.WARNING, message, throwable);
    }
}
