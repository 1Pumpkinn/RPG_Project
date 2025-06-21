package net.tyrone.rPGProject;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class QuestListener implements Listener {

    private final QuestManager questManager;

    public QuestListener(QuestManager questManager) {
        this.questManager = questManager;
    }

    private void checkProgress(Player player, String match, TrackedQuest.QuestType type) {
        UUID id = player.getUniqueId();
        if (!questManager.hasActiveQuest(id)) return;

        TrackedQuest quest = questManager.getQuest(id);
        if (quest.getType() != type) return;

        boolean done = quest.incrementIfMatch(match);
        player.sendMessage("§eProgress: " + quest.getProgressText());

        if (done) {
            player.sendMessage("§aQuest completed!");
            player.giveExp(200); // Reasonable XP
            questManager.completeQuest(id);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String blockName = event.getBlock().getType().name();
        checkProgress(event.getPlayer(), blockName, TrackedQuest.QuestType.BLOCK_BREAK);
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) return;
        String mobName = event.getEntityType().name();
        checkProgress(player, mobName, TrackedQuest.QuestType.MOB_KILL);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item item)) return;

        ItemStack caught = item.getItemStack();
        Material type = caught.getType();
        if (type == Material.COD) {
            checkProgress(event.getPlayer(), "COD", TrackedQuest.QuestType.ITEM_COLLECT);
        }
    }
}
