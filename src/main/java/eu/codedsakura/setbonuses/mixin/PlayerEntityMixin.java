package eu.codedsakura.setbonuses.mixin;

import eu.codedsakura.setbonuses.EnchantmentFactory;
import eu.codedsakura.setbonuses.SetBonuses;
import eu.codedsakura.setbonuses.VirtualEnchantment;
import eu.codedsakura.setbonuses.config.ConfigEnchant;
import eu.codedsakura.setbonuses.IPlayerEnchantmentToggle;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Random;

import static eu.codedsakura.setbonuses.SetBonuses.CONFIG;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IPlayerEnchantmentToggle {
    private final int offset = new Random().nextInt(CONFIG.updateInterval);
    private final HashSet<String> enchantmentEffects = new HashSet<>();
    public final HashSet<String> disabledEnchantments = new HashSet<>();

    private int lastHash = 0;

//    @Shadow public int experienceLevel;

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        int newHash = self.getInventory().armor.hashCode();
        if (newHash != lastHash) {
            lastHash = newHash;
            SetBonuses.logger.info("{} armor change", self.getEntityName());
            SetBonuses.logger.info("{} offset: ", offset);
            updateEnchantEffects();
        } else if (self.getServer() != null && self.getServer().getTicks() % CONFIG.updateInterval == offset) {
            updateEnchantEffects();
        }
    }

    private void updateEnchantEffects() {
        PlayerEntity self = (PlayerEntity) (Object) this;
        HashSet<String> currentEffects = new HashSet<>();

        for (VirtualEnchantment enchant : EnchantmentFactory.enchantments) {
            if (enchant.enchant.effects.length != 0 && !disabledEnchantments.contains(enchant.enchant.id)) {
                int effectStrength = 0;
                switch (enchant.enchant.stacking) {
                    case MAX:
                        effectStrength = self.getInventory().armor.stream()
                                .map(itemStack -> EnchantmentHelper.getLevel(enchant, itemStack)).max(Integer::compareTo).orElse(0);
                        break;
                    case ADDITIVE:
                        effectStrength = Math.round(self.getInventory().armor.stream()
                                .map(itemStack -> EnchantmentHelper.getLevel(enchant, itemStack)).reduce(0, Integer::sum) / 4f);
                        break;
                    case MULTIPLICATIVE:
                        effectStrength = Math.round(self.getInventory().armor.stream()
                                .map(itemStack -> EnchantmentHelper.getLevel(enchant, itemStack)).reduce(1, Math::multiplyExact) / 4f);
                        break;
                }

                if (effectStrength < 1) continue;

                for (ConfigEnchant.Effect effect : enchant.enchant.effects) {
                    currentEffects.add(effect.id);
                    self.addStatusEffect(new StatusEffectInstance(
                            Registry.STATUS_EFFECT.get(Identifier.tryParse(effect.id)), effect.duration + (CONFIG.updateInterval / 2),
                            effectStrength - 1, effect.ambient, effect.showParticles, effect.showIcon));
                }
            }
        }

        for (String prevEffect : enchantmentEffects) {
            if (!currentEffects.contains(prevEffect)) {
                self.removeStatusEffect(Registry.STATUS_EFFECT.get(Identifier.tryParse(prevEffect)));
            }
        }

        enchantmentEffects.clear();
        enchantmentEffects.addAll(currentEffects);
    }

    @Override
    public void toggle(String name) {
        if (disabledEnchantments.contains(name)) {
            disabledEnchantments.remove(name);
            ((PlayerEntity) (Object) this).sendMessage(new LiteralText(name + " enabled!"), false);
        } else {
            disabledEnchantments.add(name);
            ((PlayerEntity) (Object) this).sendMessage(new LiteralText(name + " disabled!"), false);
        }
        updateEnchantEffects();
    }
}
