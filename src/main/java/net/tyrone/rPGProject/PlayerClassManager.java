package net.tyrone.rPGProject;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerClassManager {

    private final Map<UUID, String> playerClasses = new HashMap<>();
    private final List<String> validClasses = Arrays.asList("warrior", "mage", "archer", "rogue", "healer");

    public boolean handleCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /chooseclass <" + String.join("|", validClasses) + ">");
            return true;
        }

        String chosen = args[0].toLowerCase();
        if (!validClasses.contains(chosen)) {
            player.sendMessage(ChatColor.RED + "Invalid class. Choose one of: " + String.join(", ", validClasses));
            return true;
        }

        playerClasses.put(player.getUniqueId(), chosen);
        player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.BOLD + chosen.toUpperCase());
        return true;
    }

    public Optional<String> getPlayerClass(Player player) {
        return Optional.ofNullable(playerClasses.get(player.getUniqueId()));
    }
}
