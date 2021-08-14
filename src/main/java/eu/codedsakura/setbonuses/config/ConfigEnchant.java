package eu.codedsakura.setbonuses.config;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;

public class ConfigEnchant {
    public boolean enabled = true;
    public String id;
    public int levels = 1;
    public boolean toggleable = true;
    public EquipmentSlot[] slots;
    public Enchantment.Rarity rarity = Enchantment.Rarity.UNCOMMON;
    public Power power = new Power();
    public String[] effects;
    public Stacking stacking = Stacking.NONE;

    public enum Stacking {
        NONE, ADDITIVE, MULTIPLICATIVE
    }
    public static class Power {
        public int base = 5;
        public int delta = 5;
        public int increment = 5;
    }
}
