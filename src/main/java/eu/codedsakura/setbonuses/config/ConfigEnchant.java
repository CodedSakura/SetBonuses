package eu.codedsakura.setbonuses.config;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
    public Effect[] effects = new Effect[0];
    public ArmorMaterials[] materials = new ArmorMaterials[0];

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

        protected void verify() {
            if (Identifier.tryParse(id) == null) throw new NullPointerException("Effect ID is null!");
            if (Registry.STATUS_EFFECT.get(Identifier.tryParse(id)) == null) throw new NullPointerException("Effect not found!");
        }
    }

    public void verify() {
        if (id == null) throw new NullPointerException("ID is null!");
        if (slots.length == 0) throw new NullPointerException("Specify at least one slot!");
        for (EquipmentSlot slot : slots) {
            if (slot == null) throw new NullPointerException("EquipmentSlot is null!");
        }
        if (target == null) throw new NullPointerException("Target is null!");
        if (rarity == null) throw new NullPointerException("Rarity is null!");
        if (power == null) throw new NullPointerException("Power is null!");
        if (stacking == null) throw new NullPointerException("Stacking is null!");
        if (effects == null) effects = new Effect[0];
        for (Effect effect : effects) effect.verify();
    }
}
