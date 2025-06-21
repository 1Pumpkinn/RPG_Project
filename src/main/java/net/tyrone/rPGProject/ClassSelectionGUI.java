package net.tyrone.rPGProject;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ClassSelectionGUI {

    public static void openClassSelection(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "Choose Your Class");

        // Warrior
        ItemStack warrior = new ItemStack(Material.IRON_SWORD);
        ItemMeta warriorMeta = warrior.getItemMeta();
        warriorMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "WARRIOR");
        warriorMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "A mighty melee combatant",
                "",
                ChatColor.YELLOW + "Primary Stats:",
                ChatColor.WHITE + "• High Health & Defense",
                ChatColor.WHITE + "• Sword & Axe Mastery",
                ChatColor.WHITE + "• Damage Resistance",
                "",
                ChatColor.GREEN + "Starting Skills:",
                ChatColor.WHITE + "• Slash (Active)",
                ChatColor.WHITE + "• Toughness (Passive)",
                "",
                ChatColor.AQUA + "Click to select!"
        ));
        warrior.setItemMeta(warriorMeta);

        // Mage
        ItemStack mage = new ItemStack(Material.BLAZE_ROD);
        ItemMeta mageMeta = mage.getItemMeta();
        mageMeta.setDisplayName(ChatColor.BLUE + ChatColor.BOLD.toString() + "MAGE");
        mageMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Master of arcane arts",
                "",
                ChatColor.YELLOW + "Primary Stats:",
                ChatColor.WHITE + "• High Mana & Magic Power",
                ChatColor.WHITE + "• Spell Casting",
                ChatColor.WHITE + "• Area Damage",
                "",
                ChatColor.GREEN + "Starting Skills:",
                ChatColor.WHITE + "• Fireball (Active)",
                ChatColor.WHITE + "• Mana Shield (Passive)",
                "",
                ChatColor.AQUA + "Click to select!"
        ));
        mage.setItemMeta(mageMeta);

        // Archer
        ItemStack archer = new ItemStack(Material.BOW);
        ItemMeta archerMeta = archer.getItemMeta();
        archerMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "ARCHER");
        archerMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Swift ranged specialist",
                "",
                ChatColor.YELLOW + "Primary Stats:",
                ChatColor.WHITE + "• High Speed & Accuracy",
                ChatColor.WHITE + "• Bow Mastery",
                ChatColor.WHITE + "• Critical Strikes",
                "",
                ChatColor.GREEN + "Starting Skills:",
                ChatColor.WHITE + "• Power Shot (Active)",
                ChatColor.WHITE + "• Eagle Eye (Passive)",
                "",
                ChatColor.AQUA + "Click to select!"
        ));
        archer.setItemMeta(archerMeta);

        // Rogue
        ItemStack rogue = new ItemStack(Material.IRON_NUGGET);
        ItemMeta rogueMeta = rogue.getItemMeta();
        rogueMeta.setDisplayName(ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "ROGUE");
        rogueMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Stealthy assassin",
                "",
                ChatColor.YELLOW + "Primary Stats:",
                ChatColor.WHITE + "• High Stealth & Agility",
                ChatColor.WHITE + "• Dagger Mastery",
                ChatColor.WHITE + "• Backstab Damage",
                "",
                ChatColor.GREEN + "Starting Skills:",
                ChatColor.WHITE + "• Stealth (Active)",
                ChatColor.WHITE + "• Poison Blade (Passive)",
                "",
                ChatColor.AQUA + "Click to select!"
        ));
        rogue.setItemMeta(rogueMeta);

        // Healer
        ItemStack healer = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta healerMeta = healer.getItemMeta();
        healerMeta.setDisplayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "HEALER");
        healerMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Divine support specialist",
                "",
                ChatColor.YELLOW + "Primary Stats:",
                ChatColor.WHITE + "• High Mana & Wisdom",
                ChatColor.WHITE + "• Healing Power",
                ChatColor.WHITE + "• Support Abilities",
                "",
                ChatColor.GREEN + "Starting Skills:",
                ChatColor.WHITE + "• Heal (Active)",
                ChatColor.WHITE + "• Regeneration (Passive)",
                "",
                ChatColor.AQUA + "Click to select!"
        ));
        healer.setItemMeta(healerMeta);

        // Place items in GUI
        gui.setItem(10, warrior);
        gui.setItem(12, mage);
        gui.setItem(14, archer);
        gui.setItem(16, rogue);
        gui.setItem(28, healer);

        // Add decorative glass panes
        ItemStack glass = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        for (int i = 0; i < 45; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, glass);
            }
        }

        player.openInventory(gui);
    }
}