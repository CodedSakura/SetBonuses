package eu.codedsakura.setbonuses.config;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class ConfigEnchant {
    public boolean enabled = true;
    public String id;
    public int levels = 1;
    public boolean toggleable = true;
    public boolean treasure = false;
    public boolean cursed = false;
    public boolean forEnchantedBook = true;
    public boolean forRandomSelection = true;
    public EquipmentSlot[] slots = new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public EnchantmentTarget target = EnchantmentTarget.ARMOR;
    public Enchantment.Rarity rarity = Enchantment.Rarity.UNCOMMON;
    public Power power = new Power();
    public Stacking stacking = Stacking.MAX;
    public Effect[] effects;

    public enum Stacking {
        MAX, ADDITIVE, MULTIPLICATIVE
    }
    public static class Power {
        public int base = 5;
        public int delta = 5;
        public int increment = 5;
    }
    public static class Effect {
        public String id;
        public int duration = 210;
        public boolean ambient = false;
        public boolean showParticles = false;
        public boolean showIcon = true;
    }
}
