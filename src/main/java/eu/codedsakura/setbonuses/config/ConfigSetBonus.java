package eu.codedsakura.setbonuses.config;

import com.google.gson.annotations.Expose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;

public class ConfigSetBonus {
    @Expose public boolean enabled = true;
    @Expose public Effect[] effects = new Effect[0];
    @Expose public float toughness = 0;
    @Expose public float protection = 0;
    @Expose public float knockbackResistance = 0;
    @Expose public Partial partial = Partial.OFF;
    @Expose public String material;

    public enum Partial {
        OFF, REDUCED_3, MISSING_CHEST
    }

    public long getNeededCount() {
        switch (partial) {
            case OFF:
                return 4;
            case REDUCED_3:
            case MISSING_CHEST:
                return 3;
        }
        return Integer.MAX_VALUE;
    }

    public int getStrength(Effect effect, PlayerEntity player, long pieces) {
        switch (partial) {
            case OFF:
                return effect.strength;
            case REDUCED_3:
                if (pieces < 3) return -1;
                return pieces == 3 ? effect.strength / 2 : effect.strength;
            case MISSING_CHEST:
                if (pieces < 3) return -1;
                if (pieces == 4) return effect.strength;
                if (player.getInventory().armor.get(1).getItem() instanceof ArmorItem) {
                    if (((ArmorItem) player.getInventory().armor.get(1).getItem()).getMaterial().getName().toUpperCase().equals(material)) {
                        return -1;
                    }
                }
                return effect.strength;
        }
        return effect.strength;
    }

    public static class Effect extends ConfigEnchant.Effect {
        public int strength = 0;
    }

    public void verify() {
        if (effects == null) effects = new Effect[0];
        for (Effect effect : effects) effect.verify();
        if (partial == null) throw new NullPointerException("Partial is null!");
        if (material == null) throw new NullPointerException("Material is null!");
    }
}
