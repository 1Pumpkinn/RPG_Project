package net.tyrone.rPGProject;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class SkillSystem {

    private final JavaPlugin plugin;
    private final PlayerClassManager classManager;

    public SkillSystem(JavaPlugin plugin, PlayerClassManager classManager) {
        this.plugin = plugin;
        this.classManager = classManager;
    }

    public boolean useSkill(Player player, String skillName) {
        PlayerClass playerClass = classManager.getPlayerClass(player).orElse(null);
        if (playerClass == null) {
            player.sendMessage(ChatColor.RED + "You must select a class first!");
            return false;
        }

        int skillLevel = playerClass.getSkillLevel(skillName);
        if (skillLevel <= 0) {
            player.sendMessage(ChatColor.RED + "You don't have access to this skill!");
            return false;
        }

        if (playerClass.isSkillOnCooldown(skillName)) {
            player.sendMessage(ChatColor.RED + "This skill is on cooldown!");
            return false;
        }

        return executeSkill(player, playerClass, skillName.toLowerCase(), skillLevel);
    }

    private boolean executeSkill(Player player, PlayerClass playerClass, String skillName, int level) {
        switch (skillName) {
            case "slash":            return executeSlash(player, level);
            case "berserker_rage":   return executeBerserkerRage(player, level);
            case "shield_wall":      return executeShieldWall(player, level);
            case "fireball":         return executeFireball(player, level);
            case "lightning_bolt":   return executeLightningBolt(player, level);
            case "teleport":         return executeTeleport(player, level);
            case "power_shot":       return executePowerShot(player, level);
            case "multishot":        return executeMultishot(player, level);
            case "explosive_arrow":  return executeExplosiveArrow(player, level);
            case "stealth":          return executeStealth(player, level);
            case "backstab":         return executeBackstab(player, level);
            case "shadow_step":      return executeShadowStep(player, level);
            case "heal":             return executeHeal(player, level);
            case "group_heal":       return executeGroupHeal(player, level);
            case "sanctuary":        return executeSanctuary(player, level);
            default:                 return false;
        }
    }

    // WARRIOR SKILLS
    private boolean executeSlash(Player player, int level) {
        double damage = 5 + (level * 2);
        double radius = 2 + (level * 0.5);

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity target = (LivingEntity) entity;
                target.damage(damage, player);
                target.getWorld().spawnParticle(Particle.SWEEP_ATTACK,
                        target.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0);
            }
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
        player.sendMessage(ChatColor.RED + "Slash activated!");

        classManager.getPlayerClass(player).get()
                .setSkillCooldown("slash", 8000 - (level * 1000));

        return true;
    }

    private boolean executeBerserkerRage(Player player, int level) {
        int duration = 10 + (level * 5);
        int strength = level - 1;

        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration * 20, strength, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, 0, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration * 20, 0, false, true));

        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 20, 1, 1, 1, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.0f, 0.7f);
        player.sendMessage(ChatColor.DARK_RED + "BERSERKER RAGE ACTIVATED!");

        classManager.getPlayerClass(player).get().setSkillCooldown("berserker_rage", 60000);
        return true;
    }

    private boolean executeShieldWall(Player player, int level) {
        int duration = 5 + (level * 3);
        int resistance = level;

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration * 20, resistance, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration * 20, 1, false, true));

        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 1, 0), 10, 1, 1, 1, 0);
        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
        player.sendMessage(ChatColor.BLUE + "Shield Wall activated!");

        classManager.getPlayerClass(player).get().setSkillCooldown("shield_wall", 30000);
        return true;
    }

    // MAGE SKILLS
    private boolean executeFireball(Player player, int level) {
        var fireball = player.launchProjectile(org.bukkit.entity.Fireball.class);
        fireball.setYield(level * 2);
        fireball.setVelocity(player.getLocation().getDirection().multiply(2));

        player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.0f);
        player.sendMessage(ChatColor.GOLD + "Fireball launched!");

        classManager.getPlayerClass(player).get().setSkillCooldown("fireball", 5000 - (level * 500));
        return true;
    }

    private boolean executeLightningBolt(Player player, int level) {
        Location target = player.getTargetBlock(null, 20 + (level * 5)).getLocation();
        for (int i = 0; i < level; i++) {
            Location strikeLocation = target.clone().add(
                    (Math.random() - 0.5) * 4, 0, (Math.random() - 0.5) * 4);
            player.getWorld().strikeLightning(strikeLocation);
        }

        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        player.sendMessage(ChatColor.YELLOW + "Lightning Strike!");

        classManager.getPlayerClass(player).get().setSkillCooldown("lightning_bolt", 15000);
        return true;
    }

    private boolean executeTeleport(Player player, int level) {
        int range = 5 + (level * 3);
        Location target = player.getTargetBlock(null, range).getLocation().add(0, 1, 0);

        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 20, 1, 1, 1, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        player.teleport(target);

        player.getWorld().spawnParticle(Particle.PORTAL, target, 20, 1, 1, 1, 0.1);
        player.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleported!");

        classManager.getPlayerClass(player).get().setSkillCooldown("teleport", 10000 - (level * 1000));
        return true;
    }

    // ARCHER SKILLS
    private boolean executePowerShot(Player player, int level) {
        var arrow = player.launchProjectile(org.bukkit.entity.Arrow.class);
        arrow.setVelocity(player.getLocation().getDirection().multiply(3 + level));
        arrow.setDamage(arrow.getDamage() * (1 + level * 0.5));
        arrow.setCritical(true);

        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 0.8f);
        player.sendMessage(ChatColor.GREEN + "Power Shot!");

        classManager.getPlayerClass(player).get().setSkillCooldown("power_shot", 6000 - (level * 800));
        return true;
    }

    private boolean executeMultishot(Player player, int level) {
        int arrows = 3 + level;
        Vector direction = player.getLocation().getDirection();

        for (int i = 0; i < arrows; i++) {
            var arrow = player.launchProjectile(org.bukkit.entity.Arrow.class);
            Vector spread = direction.clone().rotateAroundY(Math.toRadians((i - arrows / 2) * 15));
            arrow.setVelocity(spread.multiply(2));
        }

        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.2f);
        player.sendMessage(ChatColor.GREEN + "Multishot activated!");

        classManager.getPlayerClass(player).get().setSkillCooldown("multishot", 12000);
        return true;
    }

    private boolean executeExplosiveArrow(Player player, int level) {
        var arrow = player.launchProjectile(org.bukkit.entity.Arrow.class);
        arrow.setVelocity(player.getLocation().getDirection().multiply(2.5));
        arrow.setMetadata("explosive", new FixedMetadataValue(plugin, level * 2));

        player.playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 1.0f, 1.0f);
        player.sendMessage(ChatColor.RED + "Explosive Arrow fired!");

        classManager.getPlayerClass(player).get().setSkillCooldown("explosive_arrow", 20000);
        return true;
    }

    // ROGUE SKILLS
    private boolean executeStealth(Player player, int level) {
        int duration = 5 + (level * 3);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration * 20, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, level - 1, false, false));

        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 20, 1, 1, 1, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0f, 0.5f);
        player.sendMessage(ChatColor.DARK_GRAY + "Stealth activated!");

        classManager.getPlayerClass(player).get().setSkillCooldown("stealth", 25000);
        return true;
    }

    private boolean executeBackstab(Player player, int level) {
        double damage = 8 + (level * 3);

        for (Entity entity : player.getNearbyEntities(2, 2, 2)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity target = (LivingEntity) entity;
                Vector toTarget = target.getLocation().subtract(player.getLocation()).toVector();
                Vector targetDirection = target.getLocation().getDirection();

                if (toTarget.dot(targetDirection) > 0) {
                    target.damage(damage * 2, player);
                    target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR,
                            target.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0);
                    player.sendMessage(ChatColor.DARK_RED + "BACKSTAB! Critical hit!");
                } else {
                    target.damage(damage, player);
                    player.sendMessage(ChatColor.RED + "Backstab from the front!");
                }
            }
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
        classManager.getPlayerClass(player).get().setSkillCooldown("backstab", 10000);
        return true;
    }

    private boolean executeShadowStep(Player player, int level) {
        int range = 8 + (level * 2);
        LivingEntity target = null;
        double closest = range;

        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (entity instanceof LivingEntity && entity != player) {
                double dist = player.getLocation().distance(entity.getLocation());
                if (dist < closest) { target = (LivingEntity) entity; closest = dist; }
            }
        }

        if (target == null) {
            player.sendMessage(ChatColor.RED + "No valid target found!");
            return false;
        }

        Vector behind = target.getLocation().getDirection().multiply(-2);
        Location tpLoc = target.getLocation().add(behind).add(0, 0.5, 0);

        player.getWorld().spawnParticle(Particle.LARGE_SMOKE, player.getLocation(), 10, 1, 1, 1, 0.1);
        player.teleport(tpLoc);
        player.getWorld().spawnParticle(Particle.LARGE_SMOKE, tpLoc, 10, 1, 1, 1, 0.1);

        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
        player.sendMessage(ChatColor.DARK_PURPLE + "Shadow Step!");
        classManager.getPlayerClass(player).get().setSkillCooldown("shadow_step", 15000);
        return true;
    }

    // HEALER SKILLS
    private boolean executeHeal(Player player, int level) {
        double heal = 4 + (level * 2);
        double current = player.getHealth(); double max = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(max, current + heal));

        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 10, 1, 1, 1, 0);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        player.sendMessage(ChatColor.GREEN + "Healed " + (int) heal + " health!");
        classManager.getPlayerClass(player).get().setSkillCooldown("heal", 8000 - (level * 1000));
        return true;
    }

    private boolean executeGroupHeal(Player player, int level) {
        double heal = 3 + level; double radius = 5 + level; int count = 0;
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) {
                Player t = (Player) entity;
                double curr = t.getHealth(), mx = t.getAttribute(Attribute.MAX_HEALTH).getValue();
                t.setHealth(Math.min(mx, curr + heal));
                t.getWorld().spawnParticle(Particle.HEART, t.getLocation().add(0,1,0), 5,0.5,0.5,0.5,0);
                count++;
            }
        }
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.2f);
        player.sendMessage(ChatColor.GREEN + "Group Heal: Healed " + count + " players!");
        classManager.getPlayerClass(player).get().setSkillCooldown("group_heal", 20000);
        return true;
    }

    private boolean executeSanctuary(Player player, int level) {
        int duration = 10 + (level * 5); double radius = 3 + level;
        Location center = player.getLocation();
        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof Player) {
                Player t = (Player) e;
                t.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration * 20, 2, false, true));
                t.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration * 20, 1, false, true));
                t.sendMessage(ChatColor.GOLD + "You are protected by Sanctuary!");
            }
        }
        for (int i = 0; i < 360; i += 30) {
            double x = center.getX() + radius * Math.cos(Math.toRadians(i));
            double z = center.getZ() + radius * Math.sin(Math.toRadians(i));
            center.getWorld().spawnParticle(Particle.END_ROD, new Location(center.getWorld(), x, center.getY()+1, z), 1, 0, 0, 0, 0);
        }
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.0f);
        player.sendMessage(ChatColor.GOLD + "Sanctuary created!");
        classManager.getPlayerClass(player).get().setSkillCooldown("sanctuary", 60000);
        return true;
    }
}
