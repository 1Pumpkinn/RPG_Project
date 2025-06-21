package net.tyrone.rPGProject;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class SpawnQuestVillagerCommand implements CommandExecutor {

    private final RPGProject plugin;

    public SpawnQuestVillagerCommand(RPGProject plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this.");
            return true;
        }

        Villager villager = player.getWorld().spawn(player.getLocation(), Villager.class);
        villager.setCustomName(ChatColor.LIGHT_PURPLE + "Quest Villager");
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);

        QuestVillagerTag.markAsQuestVillager(villager);
        player.sendMessage(ChatColor.GREEN + "Spawned a quest villager!");

        return true;
    }
}
