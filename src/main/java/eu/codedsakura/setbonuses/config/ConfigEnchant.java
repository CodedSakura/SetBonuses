package eu.codedsakura.setbonuses.config;

import net.minecraft.entity.EquipmentSlot;

public class ConfigEnchant {
    boolean enabled;
    String id;
    String name;
    int levels = 1;
    boolean toggleable = true;
    EquipmentSlot[] slots;
    String[] effects;
    Stacking stacking = Stacking.NONE;

    public enum Stacking {
        NONE, ADDITIVE, MULTIPLICATIVE
    }
}
