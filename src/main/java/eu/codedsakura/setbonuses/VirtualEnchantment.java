package eu.codedsakura.setbonuses;

import eu.pb4.polymer.interfaces.VirtualObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public abstract class VirtualEnchantment extends Enchantment implements VirtualObject {
    protected VirtualEnchantment(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }
}
