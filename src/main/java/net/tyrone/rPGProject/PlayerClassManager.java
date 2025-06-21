package net.tyrone.rPGProject;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerClassManager {

    private final JavaPlugin plugin;
    private final Map<UUID, PlayerClass> playerClasses = new HashMap<>();
    private final Set<UUID> newPlayers = new HashSet<>();
    private File dataFile;
    private FileConfiguration data;

    public PlayerClassManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadData();
    }

    private void loadData() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml");
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        loadPlayerClasses();
    }

    private void loadPlayerClasses() {
        for (String uuidString : data.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                String className = data.getString(uuidString + ".class");
                int level = data.getInt(uuidString + ".level", 1);
                int experience = data.getInt(uuidString + ".experience", 0);

                if (className != null) {
                    PlayerClass.ClassType type = PlayerClass.ClassType.valueOf(className.toUpperCase());
                    PlayerClass playerClass = new PlayerClass(type);

                    // Load skills
                    if (data.contains(uuidString + ".skills")) {
                        for (String skillName : data.getConfigurationSection(uuidString + ".skills").getKeys(false)) {
                            int skillLevel = data.getInt(uuidString + ".skills." + skillName);
                            playerClass.getSkillLevels().put(skillName, skillLevel);
                        }
                    }

                    // Set level and experience using reflection or setter methods
                    playerClass.addExperience(experience);

                    playerClasses.put(uuid, playerClass);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load player data for: " + uuidString);
            }
        }
    }

    public void saveData() {
        for (Map.Entry<UUID, PlayerClass> entry : playerClasses.entrySet()) {
            String uuidString = entry.getKey().toString();
            PlayerClass playerClass = entry.getValue();

            data.set(uuidString + ".class", playerClass.getType().name());
            data.set(uuidString + ".level", playerClass.getLevel());
            data.set(uuidString + ".experience", playerClass.getExperience());

            // Save skills
            for (Map.Entry<String, Integer> skill : playerClass.getSkillLevels().entrySet()) {
                data.set(uuidString + ".skills." + skill.getKey(), skill.getValue());
            }
        }

        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml");
        }
    }

    public void handlePlayerJoin(Player player) {
        UUID uuid = player.getUniqueId();

        if (!playerClasses.containsKey(uuid)) {
            newPlayers.add(uuid);

            // Delay the GUI opening to ensure player is fully loaded
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline() && newPlayers.contains(uuid)) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Welcome to the RPG Server!");
                    player.sendMessage(ChatColor.YELLOW + "Please choose your class to begin your adventure!");
                    ClassSelectionGUI.openClassSelection(player);
                }
            }, 20L); // 1 second delay
        } else {
            // Apply existing class bonuses
            PlayerClass playerClass = playerClasses.get(uuid);
            playerClass.applyClassBonuses(player);

            player.sendMessage(ChatColor.GREEN + "Welcome back, " +
                    ChatColor.BOLD + playerClass.getType().getDisplayName() +
                    ChatColor.GREEN + " (Level " + playerClass.getLevel() + ")!");
        }
    }

    public boolean selectClass(Player player, PlayerClass.ClassType classType) {
        UUID uuid = player.getUniqueId();

        if (playerClasses.containsKey(uuid)) {
            return false; // Already has a class
        }

        PlayerClass playerClass = new PlayerClass(classType);
        playerClasses.put(uuid, playerClass);
        newPlayers.remove(uuid);

        // Apply class bonuses
        playerClass.applyClassBonuses(player);

        // Welcome message
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Class Selected!");
        player.sendMessage(ChatColor.YELLOW + "You are now a " +
                ChatColor.BOLD + classType.getDisplayName() + ChatColor.YELLOW + "!");
        player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.WHITE + "/skills" +
                ChatColor.GRAY + " to view your abilities.");
        player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.WHITE + "/stats" +
                ChatColor.GRAY + " to view your character information.");

        // Save immediately
        saveData();

        return true;
    }

    public boolean handleStatsCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        PlayerClass playerClass = playerClasses.get(player.getUniqueId());
        if (playerClass == null) {
            player.sendMessage(ChatColor.RED + "You haven't selected a class yet!");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.BOLD + "CHARACTER STATS" + ChatColor.GOLD + " ===");
        player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.WHITE + playerClass.getType().getDisplayName());
        player.sendMessage(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + playerClass.getLevel());
        player.sendMessage(ChatColor.YELLOW + "Experience: " + ChatColor.WHITE + playerClass.getExperience() +
                ChatColor.GRAY + "/" + (playerClass.getLevel() * 100));
        player.sendMessage(ChatColor.YELLOW + "Health: " + ChatColor.WHITE + (int)player.getHealth() +
                "/" + (int)player.getAttribute(Attribute.MAX_HEALTH).getValue());

        return true;
    }

    public boolean handleSkillsCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        PlayerClass playerClass = playerClasses.get(player.getUniqueId());
        if (playerClass == null) {
            player.sendMessage(ChatColor.RED + "You haven't selected a class yet!");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.BOLD + "YOUR SKILLS" + ChatColor.GOLD + " ===");

        for (Map.Entry<String, Integer> entry : playerClass.getSkillLevels().entrySet()) {
            String skillName = entry.getKey();
            int level = entry.getValue();

            if (level > 0) {
                String displayName = formatSkillName(skillName);
                String levelBar = getSkillLevelBar(level);
                player.sendMessage(ChatColor.AQUA + displayName + ChatColor.WHITE + " " + levelBar +
                        ChatColor.GRAY + " (Level " + level + "/5)");
            }
        }

        player.sendMessage(ChatColor.GRAY + "Tip: Skills improve as you use them and level up!");
        return true;
    }

    private String formatSkillName(String skillName) {
        return Arrays.stream(skillName.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .reduce((a, b) -> a + " " + b)
                .orElse(skillName);
    }

    private String getSkillLevelBar(int level) {
        StringBuilder bar = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= level) {
                bar.append(ChatColor.GREEN + "█");
            } else {
                bar.append(ChatColor.DARK_GRAY + "█");
            }
        }
        return bar.toString();
    }

    public void addExperience(Player player, int amount) {
        PlayerClass playerClass = playerClasses.get(player.getUniqueId());
        if (playerClass != null) {
            int oldLevel = playerClass.getLevel();
            playerClass.addExperience(amount);

            if (playerClass.getLevel() > oldLevel) {
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "LEVEL UP!");
                player.sendMessage(ChatColor.YELLOW + "You are now level " + playerClass.getLevel() + "!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

                // Reapply bonuses for new level
                playerClass.applyClassBonuses(player);

                // Auto-save on level up
                saveData();
            }
        }
    }

    // Legacy method for compatibility
    public boolean handleCommand(CommandSender sender, String[] args) {
        return handleStatsCommand(sender, args);
    }

    public Optional<PlayerClass> getPlayerClass(Player player) {
        return Optional.ofNullable(playerClasses.get(player.getUniqueId()));
    }

    public boolean hasClass(Player player) {
        return playerClasses.containsKey(player.getUniqueId());
    }

    public void handlePlayerQuit(Player player) {
        saveData(); // Save when player leaves
    }
}