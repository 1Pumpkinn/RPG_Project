package net.tyrone.rPGProject;

import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class QuestVillagerTag {

    private static NamespacedKey key;

    public static void init(Plugin plugin) {
        key = new NamespacedKey(plugin, "quest_villager");
    }

    public static void markAsQuestVillager(Entity entity) {
        PersistentDataContainer data = entity.getPersistentDataContainer();
        data.set(key, PersistentDataType.INTEGER, 1);
    }

    public static boolean isQuestVillager(Entity entity) {
        PersistentDataContainer data = entity.getPersistentDataContainer();
        return data.has(key, PersistentDataType.INTEGER);
    }
}
