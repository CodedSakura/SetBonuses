package eu.codedsakura.setbonuses;

import eu.codedsakura.setbonuses.config.ConfigEnchant;
import eu.pb4.polymer.interfaces.VirtualObject;
import net.minecraft.enchantment.Enchantment;

public class VirtualEnchantment extends Enchantment implements VirtualObject {
    public final ConfigEnchant enchant;

    VirtualEnchantment(ConfigEnchant enchant) {
        super(enchant.rarity, enchant.target, enchant.slots);
        this.enchant = enchant;
    }

    @Override
    public int getMinPower(int level) {
        return this.enchant.power.base + (level - 1) * this.enchant.power.increment;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + this.enchant.power.delta;
    }

    @Override
    public int getMaxLevel() {
        return this.enchant.levels;
    }
}
