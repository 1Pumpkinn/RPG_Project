package net.tyrone.rPGProject;

import org.bukkit.plugin.java.JavaPlugin;

public class RPGProject extends JavaPlugin {

    private QuestManager questManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        // Initialize managers
        this.messageManager = new MessageManager(this);
        this.questManager = new QuestManager(messageManager);

        // Initialize any static systems
        QuestVillagerTag.init(this);

        // Register commands
        getCommand("spawnquestvillager").setExecutor(new SpawnQuestVillagerCommand(this));

        // Register event listeners
        getServer().getPluginManager().registerEvents(new QuestListener(questManager), this);
        getServer().getPluginManager().registerEvents(new VillagerListener(questManager, messageManager), this);

        // Start repeating tasks
        getServer().getScheduler().runTaskTimer(this, new ParticleTask(), 0L, 40L);
    }
}