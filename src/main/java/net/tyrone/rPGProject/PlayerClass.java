package net.tyrone.rPGProject;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class PlayerClass {

    public enum ClassType {
        WARRIOR("Warrior", Material.IRON_SWORD),
        MAGE("Mage", Material.BLAZE_ROD),
        ARCHER("Archer", Material.BOW),
        ROGUE("Rogue", Material.IRON_NUGGET),
        HEALER("Healer", Material.GOLDEN_APPLE);

        private final String displayName;
        private final Material icon;

        ClassType(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }

        public String getDisplayName() { return displayName; }
        public Material getIcon() { return icon; }
    }

    private final ClassType type;
    private int level;
    private int experience;
    private final Map<String, Integer> skillLevels;
    private final Map<String, Long> skillCooldowns;

    // Base stats per class
    private static final Map<ClassType, ClassStats> BASE_STATS = new HashMap<>();
    static {
        BASE_STATS.put(ClassType.WARRIOR, new ClassStats(25.0, 0.0, 8.0, 0.2, 1.2));
        BASE_STATS.put(ClassType.MAGE, new ClassStats(15.0, 100.0, 12.0, 0.0, 0.8));
        BASE_STATS.put(ClassType.ARCHER, new ClassStats(18.0, 0.0, 10.0, 0.3, 1.1));
        BASE_STATS.put(ClassType.ROGUE, new ClassStats(16.0, 0.0, 14.0, 0.4, 1.3));
        BASE_STATS.put(ClassType.HEALER, new ClassStats(20.0, 80.0, 6.0, 0.1, 0.9));
    }

    public PlayerClass(ClassType type) {
        this.type = type;
        this.level = 1;
        this.experience = 0;
        this.skillLevels = new HashMap<>();
        this.skillCooldowns = new HashMap<>();

        initializeSkills();
    }

    private void initializeSkills() {
        switch (type) {
            case WARRIOR:
                skillLevels.put("slash", 1);
                skillLevels.put("toughness", 1);
                skillLevels.put("berserker_rage", 0);
                skillLevels.put("shield_wall", 0);
                break;
            case MAGE:
                skillLevels.put("fireball", 1);
                skillLevels.put("mana_shield", 1);
                skillLevels.put("lightning_bolt", 0);
                skillLevels.put("teleport", 0);
                break;
            case ARCHER:
                skillLevels.put("power_shot", 1);
                skillLevels.put("eagle_eye", 1);
                skillLevels.put("multishot", 0);
                skillLevels.put("explosive_arrow", 0);
                break;
            case ROGUE:
                skillLevels.put("stealth", 1);
                skillLevels.put("poison_blade", 1);
                skillLevels.put("backstab", 0);
                skillLevels.put("shadow_step", 0);
                break;
            case HEALER:
                skillLevels.put("heal", 1);
                skillLevels.put("regeneration", 1);
                skillLevels.put("group_heal", 0);
                skillLevels.put("sanctuary", 0);
                break;
        }
    }

    public void applyClassBonuses(Player player) {
        ClassStats stats = BASE_STATS.get(type);

        // Set max health
        AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(stats.health + (level * 2));
        }

        // Set movement speed
        AttributeInstance speedAttr = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(stats.speed);
        }

        // Apply passive effects based on class
        applyPassiveEffects(player);

        // Give starting equipment
        giveStartingEquipment(player);
    }

    private void applyPassiveEffects(Player player) {
        switch (type) {
            case WARRIOR:
                if (skillLevels.get("toughness") > 0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,
                            Integer.MAX_VALUE, skillLevels.get("toughness") - 1, false, false));
                }
                break;
            case MAGE:
                if (skillLevels.get("mana_shield") > 0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,
                            Integer.MAX_VALUE, skillLevels.get("mana_shield") - 1, false, false));
                }
                break;
            case ARCHER:
                if (skillLevels.get("eagle_eye") > 0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,
                            Integer.MAX_VALUE, 0, false, false));
                }
                break;
            case ROGUE:
                if (skillLevels.get("poison_blade") > 0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
                            Integer.MAX_VALUE, 0, false, false));
                }
                break;
            case HEALER:
                if (skillLevels.get("regeneration") > 0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
                            Integer.MAX_VALUE, 0, false, false));
                }
                break;
        }
    }

    private void giveStartingEquipment(Player player) {
        switch (type) {
            case WARRIOR:
                player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                player.getInventory().addItem(new ItemStack(Material.LEATHER_CHESTPLATE));
                break;
            case MAGE:
                player.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
                player.getInventory().addItem(new ItemStack(Material.LEATHER_BOOTS));
                break;
            case ARCHER:
                player.getInventory().addItem(new ItemStack(Material.BOW));
                player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
                break;
            case ROGUE:
                player.getInventory().addItem(new ItemStack(Material.IRON_NUGGET));
                player.getInventory().addItem(new ItemStack(Material.LEATHER_LEGGINGS));
                break;
            case HEALER:
                player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 3));
                player.getInventory().addItem(new ItemStack(Material.LEATHER_HELMET));
                break;
        }
    }

    public void addExperience(int exp) {
        experience += exp;
        checkLevelUp();
    }

    private void checkLevelUp() {
        int requiredExp = level * 100; // 100 exp per level
        if (experience >= requiredExp) {
            level++;
            experience -= requiredExp;
            // Player will be notified by the manager
        }
    }

    public boolean isSkillOnCooldown(String skillName) {
        Long cooldownEnd = skillCooldowns.get(skillName);
        return cooldownEnd != null && System.currentTimeMillis() < cooldownEnd;
    }

    public void setSkillCooldown(String skillName, long cooldownMs) {
        skillCooldowns.put(skillName, System.currentTimeMillis() + cooldownMs);
    }

    public void upgradeSkill(String skillName) {
        int currentLevel = skillLevels.getOrDefault(skillName, 0);
        if (currentLevel < 5) { // Max skill level 5
            skillLevels.put(skillName, currentLevel + 1);
        }
    }

    // Getters
    public ClassType getType() { return type; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public Map<String, Integer> getSkillLevels() { return skillLevels; }
    public int getSkillLevel(String skillName) { return skillLevels.getOrDefault(skillName, 0); }

    // Helper class for stats
    private static class ClassStats {
        final double health, mana, damage, speed, attackSpeed;

        ClassStats(double health, double mana, double damage, double speed, double attackSpeed) {
            this.health = health;
            this.mana = mana;
            this.damage = damage;
            this.speed = speed;
            this.attackSpeed = attackSpeed;
        }
    }
}