package eu.codedsakura.setbonuses.mixin;

import eu.codedsakura.setbonuses.EnchantmentFactory;
import eu.codedsakura.setbonuses.SetBonuses;
import eu.codedsakura.setbonuses.VirtualEnchantment;
import eu.codedsakura.setbonuses.config.ConfigEnchant;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

//    @Shadow public int experienceLevel;

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        for (VirtualEnchantment enchant : EnchantmentFactory.enchantments) {

            if (enchant.enchant.effects.length != 0) {
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

//                if (self.getServer() != null && self.getServer().getTicks() % 100 == 0) {
//                    SetBonuses.logger.info("{} {} {} {}", enchant.enchant.id, enchant.enchant.slots, enchant.enchant.stacking, effectStrength);
//                }

                if (effectStrength < 1) continue;

                for (ConfigEnchant.Effect effect : enchant.enchant.effects) {
                    self.addStatusEffect(new StatusEffectInstance(
                            Registry.STATUS_EFFECT.get(Identifier.tryParse(effect.id)), effect.duration,
                            effectStrength - 1, effect.ambient, effect.showParticles, effect.showIcon));
                }
            }
        }
    }
}
