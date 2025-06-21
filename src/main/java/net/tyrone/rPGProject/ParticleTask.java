package net.tyrone.rPGProject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Villager;

public class ParticleTask implements Runnable {
    @Override
    public void run() {
        for (var world : Bukkit.getWorlds()) {
            for (Villager villager : world.getEntitiesByClass(Villager.class)) {
                if (!QuestVillagerTag.isQuestVillager(villager)) continue;
                Location loc = villager.getLocation().add(0, 2, 0);
                world.spawnParticle(Particle.HAPPY_VILLAGER, loc, 20    , 0.5, 0.5, 0.5, 0);
            }
        }
    }
}
