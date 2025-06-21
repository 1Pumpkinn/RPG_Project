package net.tyrone.rPGProject;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {

    private final PlayerClassManager classManager;

    public GUIListener(PlayerClassManager classManager) {
        this.classManager = classManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if it's the class selection GUI
        if (!event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Choose Your Class")) {
            return;
        }

        event.setCancelled(true); // Prevent item movement

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // Determine which class was selected based on the item
        PlayerClass.ClassType selectedClass = null;
        String className = null;

        switch (clickedItem.getType()) {
            case IRON_SWORD:
                selectedClass = PlayerClass.ClassType.WARRIOR;
                className = "Warrior";
                break;
            case BLAZE_ROD:
                selectedClass = PlayerClass.ClassType.MAGE;
                className = "Mage";
                break;
            case BOW:
                selectedClass = PlayerClass.ClassType.ARCHER;
                className = "Archer";
                break;
            case IRON_NUGGET:
                selectedClass = PlayerClass.ClassType.ROGUE;
                className = "Rogue";
                break;
            case GOLDEN_APPLE:
                selectedClass = PlayerClass.ClassType.HEALER;
                className = "Healer";
                break;
            default:
                return; // Clicked on glass pane or other item
        }

        // Try to select the class
        if (classManager.selectClass(player, selectedClass)) {
            player.closeInventory();

            // Success effects
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            player.sendTitle(
                    ChatColor.GOLD + "" + ChatColor.BOLD + "CLASS SELECTED!",
                    ChatColor.YELLOW + className + " - Begin your adventure!",
                    10, 70, 20
            );

            // Give some starting experience
            classManager.addExperience(player, 50);

        } else {
            player.sendMessage(ChatColor.RED + "You already have a class selected!");
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
        }
    }
}