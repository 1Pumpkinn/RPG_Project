package net.tyrone.rPGProject;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;
import java.util.UUID;

public class VillagerListener implements Listener {

    private final QuestManager questManager;
    private final MessageManager messageManager;

    public VillagerListener(QuestManager questManager, MessageManager messageManager) {
        this.questManager = questManager;
        this.messageManager = messageManager;
    }

    @EventHandler
    public void onVillagerClick(PlayerInteractEntityEvent event) {
        // ✅ Only process the MAIN HAND interaction
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (!(event.getRightClicked() instanceof Villager villager)) return;
        if (!QuestVillagerTag.isQuestVillager(villager)) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (questManager.hasActiveQuest(uuid)) {
            TrackedQuest quest = questManager.getQuest(uuid);
            String msg = messageManager.get("quest.already_active", Map.of(
                    "progress", quest.getProgressText()
            ));
            player.sendMessage(msg);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.8f);
            return;
        }

        String newQuestText = questManager.assignQuest(uuid);
        String msg = messageManager.get("villager.new_quest", Map.of(
                "description", newQuestText
        ));
        player.sendMessage(msg);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        player.giveExp(20);
    }
}