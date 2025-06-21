package net.tyrone.rPGProject;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillCommand implements CommandExecutor {

    private final SkillSystem skillSystem;
    private final PlayerClassManager classManager;

    public SkillCommand(SkillSystem skillSystem, PlayerClassManager classManager) {
        this.skillSystem = skillSystem;
        this.classManager = classManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use skills!");
            return true;
        }

        if (args.length == 0) {
            showSkillHelp(player);
            return true;
        }

        String skillName = args[0].toLowerCase();

        // Map common skill aliases
        skillName = mapSkillAlias(skillName);

        if (skillSystem.useSkill(player, skillName)) {
            // Skill executed successfully
            classManager.addExperience(player, 10); // Give XP for using skills
        } else {
            player.sendMessage(ChatColor.RED + "Could not use skill: " + skillName);
        }

        return true;
    }

    private String mapSkillAlias(String input) {
        return switch (input) {
            case "fire", "fireball" -> "fireball";
            case "lightning", "bolt" -> "lightning_bolt";
            case "tp", "teleport" -> "teleport";
            case "power", "powershot" -> "power_shot";
            case "multi", "multishot" -> "multishot";
            case "explosive", "explode" -> "explosive_arrow";
            case "invis", "invisible", "stealth" -> "stealth";
            case "back", "backstab" -> "backstab";
            case "shadow", "shadowstep" -> "shadow_step";
            case "rage", "berserker" -> "berserker_rage";
            case "shield", "wall" -> "shield_wall";
            case "group", "groupheal" -> "group_heal";
            default -> input;
        };
    }

    private void showSkillHelp(Player player) {
        PlayerClass playerClass = classManager.getPlayerClass(player).orElse(null);
        if (playerClass == null) {
            player.sendMessage(ChatColor.RED + "You must select a class first!");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.BOLD + "AVAILABLE SKILLS" + ChatColor.GOLD + " ===");
        player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.WHITE + "/skill <name>" + ChatColor.GRAY + " to activate");
        player.sendMessage("");

        // Show available skills based on class
        switch (playerClass.getType()) {
            case WARRIOR:
                showSkillInfo(player, playerClass, "slash", "Slash", "Damages nearby enemies");
                showSkillInfo(player, playerClass, "berserker_rage", "Berserker Rage", "Increases damage and speed");
                showSkillInfo(player, playerClass, "shield_wall", "Shield Wall", "Greatly increases defense");
                break;
            case MAGE:
                showSkillInfo(player, playerClass, "fireball", "Fireball", "Launches an explosive fireball");
                showSkillInfo(player, playerClass, "lightning_bolt", "Lightning Bolt", "Strikes target with lightning");
                showSkillInfo(player, playerClass, "teleport", "Teleport", "Instantly teleports to target location");
                break;
            case ARCHER:
                showSkillInfo(player, playerClass, "power_shot", "Power Shot", "Fires a powerful arrow");
                showSkillInfo(player, playerClass, "multishot", "Multishot", "Fires multiple arrows");
                showSkillInfo(player, playerClass, "explosive_arrow", "Explosive Arrow", "Fires an explosive arrow");
                break;
            case ROGUE:
                showSkillInfo(player, playerClass, "stealth", "Stealth", "Become invisible temporarily");
                showSkillInfo(player, playerClass, "backstab", "Backstab", "Deal massive damage from behind");
                showSkillInfo(player, playerClass, "shadow_step", "Shadow Step", "Teleport behind nearest enemy");
                break;
            case HEALER:
                showSkillInfo(player, playerClass, "heal", "Heal", "Restore your health");
                showSkillInfo(player, playerClass, "group_heal", "Group Heal", "Heal all nearby players");
                showSkillInfo(player, playerClass, "sanctuary", "Sanctuary", "Create a protective area");
                break;
        }
    }

    private void showSkillInfo(Player player, PlayerClass playerClass, String skillName, String displayName, String description) {
        int level = playerClass.getSkillLevel(skillName);
        if (level > 0) {
            String status = playerClass.isSkillOnCooldown(skillName) ?
                    ChatColor.RED + " (On Cooldown)" : ChatColor.GREEN + " (Ready)";
            player.sendMessage(ChatColor.AQUA + displayName + ChatColor.GRAY + " (Lv." + level + ")" +
                    status + ChatColor.GRAY + " - " + description);
        } else {
            player.sendMessage(ChatColor.DARK_GRAY + displayName + " (Locked) - " + description);
        }
    }
}