package eu.codedsakura.setbonuses;

import eu.codedsakura.setbonuses.config.ConfigEnchant;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static eu.codedsakura.setbonuses.SetBonuses.CONFIG;
import static eu.codedsakura.setbonuses.SetBonuses.logger;

public class EnchantmentFactory {
    public static List<VirtualEnchantment> enchantments = new ArrayList<>();

    public static void register() {
        enchantments.clear();
        for (ConfigEnchant enchant : CONFIG.enchantments.list) {
            if (!enchant.enabled) continue;
            logger.info("[Set Bonuses] (Enchantments) Registering {}", enchant.id);
            VirtualEnchantment enchantment = Registry.register(Registry.ENCHANTMENT, Identifier.tryParse(enchant.id),
                    new VirtualEnchantment(enchant.rarity, EnchantmentTarget.ARMOR, enchant.slots) {
                        @Override
                        public int getMinPower(int level) {
                            return enchant.power.base + (level - 1) * enchant.power.increment;
                        }

                        @Override
                        public int getMaxPower(int level) {
                            return getMinPower(level) + enchant.power.delta;
                        }

                        @Override
                        public int getMaxLevel() {
                            return enchant.levels;
                        }
                    });
            enchantments.add(enchantment);
        }

    }
}
