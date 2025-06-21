package net.tyrone.rPGProject;

import java.util.*;

public class QuestManager {

    private final Map<UUID, TrackedQuest> activeQuests = new HashMap<>();
    private final MessageManager messages;

    public QuestManager(MessageManager messages) {
        this.messages = messages;
    }

    public boolean hasActiveQuest(UUID playerId) {
        return activeQuests.containsKey(playerId);
    }

    public TrackedQuest getQuest(UUID playerId) {
        return activeQuests.get(playerId);
    }

    public String assignQuest(UUID playerId) {
        TrackedQuest quest = generateRandomQuest();
        activeQuests.put(playerId, quest);

        Map<String, String> placeholders = Map.of(
                "amount", String.valueOf(quest.getRequired()),
                "target", formatReadableName(quest.getTarget())
        );

        String path = switch (quest.getType()) {
            case BLOCK_BREAK -> "quest.assign.block_break";
            case MOB_KILL -> "quest.assign.mob_kill";
            case ITEM_COLLECT -> "quest.assign.item_collect";
        };

        return messages.get(path, placeholders);
    }

    private String formatReadableName(String raw) {
        // e.g., OAK_LOG -> "oak logs", ZOMBIE -> "zombies"
        String formatted = raw.toLowerCase().replace('_', ' ');
        if (!formatted.endsWith("s")) {
            formatted += "s";
        }
        return formatted;
    }

    public void completeQuest(UUID playerId) {
        activeQuests.remove(playerId);
    }

    private TrackedQuest generateRandomQuest() {
        int roll = new Random().nextInt(4);
        return switch (roll) {
            case 0 -> new TrackedQuest(TrackedQuest.QuestType.BLOCK_BREAK, "OAK_LOG", 10);
            case 1 -> new TrackedQuest(TrackedQuest.QuestType.MOB_KILL, "ZOMBIE", 3);
            case 2 -> new TrackedQuest(TrackedQuest.QuestType.BLOCK_BREAK, "DIAMOND_ORE", 1);
            default -> new TrackedQuest(TrackedQuest.QuestType.ITEM_COLLECT, "COD", 3);
        };
    }
}
