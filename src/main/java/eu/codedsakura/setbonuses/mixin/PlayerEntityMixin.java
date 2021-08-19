package eu.codedsakura.setbonuses.mixin;

import eu.codedsakura.setbonuses.EnchantmentFactory;
import eu.codedsakura.setbonuses.IPlayerEnchantmentToggle;
import eu.codedsakura.setbonuses.VirtualEnchantment;
import eu.codedsakura.setbonuses.config.ConfigEnchant;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Random;
import java.util.stream.Collectors;

import static eu.codedsakura.setbonuses.SetBonuses.CONFIG;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements IPlayerEnchantmentToggle {
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
            updateEnchantEffects();
        } else if (self.getServer() != null && self.getServer().getTicks() % CONFIG.updateInterval == offset) {
            updateEnchantEffects();
        }
    }


    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList list = new NbtList();
        disabledEnchantments.stream().map(NbtString::of).forEach(list::add);
        nbt.put("set-bonus:disabled-enchantments", list);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList list = nbt.getList("set-bonus:disabled-enchantments", NbtList.STRING_TYPE);
        disabledEnchantments.addAll(list.stream().map(NbtElement::asString).collect(Collectors.toList()));
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
        PlayerEntity self = ((PlayerEntity) (Object) this);
        MutableText enchantment = new TranslatableText(Util.createTranslationKey("enchantment", Identifier.tryParse(name)));
        if (disabledEnchantments.contains(name)) {
            disabledEnchantments.remove(name);
            self.sendMessage(enchantment.append(" ").append(new TranslatableText("set-bonus.enchantment.enabled")), false);
        } else {
            disabledEnchantments.add(name);
            self.sendMessage(enchantment.append(" ").append(new TranslatableText("set-bonus.enchantment.disabled")), false);
        }
        self.playSound(SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, .2f, 2);
        updateEnchantEffects();
        self.playerScreenHandler.updateToClient();
    }

    @Override
    public boolean isDisabled(String name) {
        return disabledEnchantments.contains(name);
    }
}
