package net.tyrone.rPGProject;

public class TrackedQuest {

    public enum QuestType { BLOCK_BREAK, MOB_KILL, ITEM_COLLECT }

    private final QuestType type;
    private final String target; // e.g., OAK_LOG, ZOMBIE, COD
    private final int required;
    private int progress = 0;

    public TrackedQuest(QuestType type, String target, int required) {
        this.type = type;
        this.target = target;
        this.required = required;
    }

    public QuestType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public int getRequired() {
        return required;
    }

    public int getProgress() {
        return progress;
    }

    public boolean incrementIfMatch(String input) {
        if (!input.equalsIgnoreCase(target)) return false;
        progress++;
        return isComplete();
    }

    public boolean isComplete() {
        return progress >= required;
    }

    public String getProgressText() {
        return progress + "/" + required + " " + target.toLowerCase().replace('_', ' ');
    }
}
