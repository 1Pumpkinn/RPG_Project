package net.tyrone.rPGProject;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {

    private final PlayerClassManager classManager;

    public PlayerJoinListener(PlayerClassManager classManager) {
        this.classManager = classManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        classManager.handlePlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        classManager.handlePlayerQuit(event.getPlayer());
    }
}