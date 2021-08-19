package eu.codedsakura.setbonuses;

import eu.codedsakura.setbonuses.config.ConfigEnchant;
import eu.pb4.polymer.interfaces.VirtualObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import xyz.nucleoid.packettweaker.PacketContext;

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
            if (((IPlayerEnchantmentToggle) player).isDisabled(this.enchant.id)) {
                text = text.styled(v -> v.withStrikethrough(true));
            }
        }
        return text;
    }
}
