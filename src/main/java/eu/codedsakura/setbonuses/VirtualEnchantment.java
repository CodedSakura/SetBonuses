package eu.codedsakura.setbonuses;

import eu.codedsakura.setbonuses.config.ConfigEnchant;
import eu.codedsakura.setbonuses.ducks.IPlayerEntityDuck;
import eu.pb4.polymer.interfaces.VirtualObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Arrays;

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

    @Override
    public boolean isTreasure() {
        return this.enchant.treasure;
    }

    @Override
    public boolean isCursed() {
        return this.enchant.cursed;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return this.enchant.forEnchantedBook;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return this.enchant.forRandomSelection;
    }

    @Override
    public Text getName(int level) {
        MutableText text = super.getName(level).shallowCopy();
        PlayerEntity player = PacketContext.get().getTarget();
        if (player != null) {
            if (((IPlayerEntityDuck) player).isDisabled(this.enchant.id)) {
                text = text.styled(v -> v.withStrikethrough(true));
            }
        }
        return text;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        if (this.enchant.materials.length > 0) {
            if (stack.getItem() instanceof ArmorItem) {
                String stackMaterial = ((ArmorItem) stack.getItem()).getMaterial().getName().toUpperCase();
                return Arrays.stream(this.enchant.materials).anyMatch(material -> material.compareTo(stackMaterial) == 0);
            }
            return false;
        }
        return super.isAcceptableItem(stack);
    }
}
