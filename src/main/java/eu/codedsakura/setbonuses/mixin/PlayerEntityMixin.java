package eu.codedsakura.setbonuses.mixin;

import eu.codedsakura.setbonuses.EnchantmentFactory;
import eu.codedsakura.setbonuses.VirtualEnchantment;
import eu.codedsakura.setbonuses.config.ConfigEnchant;
import eu.codedsakura.setbonuses.config.ConfigSetBonus;
import eu.codedsakura.setbonuses.ducks.IPlayerEntityDuck;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
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

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static eu.codedsakura.setbonuses.SetBonuses.CONFIG;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements IPlayerEntityDuck {
    private final int enchantmentTickOffset = new Random().nextInt(CONFIG.updateInterval);
    private final int setBonusTickOffset = enchantmentTickOffset;
//    private final int setBonusTickOffset = new Random().nextInt(CONFIG.updateInterval);
    private final HashSet<String> enchantmentEffects = new HashSet<>();
    private final HashSet<String> disabledEnchantments = new HashSet<>();
    private final HashSet<String> setBonusEffects = new HashSet<>();

    private float[] armorBuffs = (float[]) Array.newInstance(Float.TYPE, 3);
    private int lastArmorHash = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        int newHash = self.getInventory().armor.hashCode();
        if (newHash != lastArmorHash) {
            lastArmorHash = newHash;
            updateEnchantEffects();
            updateSetBonuses();
            self.playerScreenHandler.updateToClient();
        } else if (self.getServer() != null) {
            if (self.getServer().getTicks() % CONFIG.updateInterval == enchantmentTickOffset) {
                updateEnchantEffects();
            }
            if (CONFIG.setBonuses.enabled && self.getServer().getTicks() % CONFIG.updateInterval == setBonusTickOffset) {
                updateSetBonuses();
            }
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
                    StatusEffect status = Registry.STATUS_EFFECT.get(Identifier.tryParse(effect.id));
                    if(!effect.waitDuration || !self.hasStatusEffect(status)) {
                        self.addStatusEffect(new StatusEffectInstance(
                                status, effect.duration + (CONFIG.updateInterval / 2),
                                effectStrength - 1, effect.ambient, effect.showParticles, effect.showIcon));
                    }
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

    private void updateSetBonuses() {
        PlayerEntity self = (PlayerEntity) (Object) this;
        Map<String, Long> armorMap = self.getInventory().armor.stream()
                .map(ItemStack::getItem)
                .filter(item -> item instanceof ArmorItem)
                .map(item -> ((ArmorItem) item).getMaterial().getName().toUpperCase())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        HashSet<String> currentEffects = new HashSet<>();
        armorBuffs = new float[] {0, 0, 0};

        for (ConfigSetBonus setBonus : CONFIG.setBonuses.list) {
            if (setBonus.material == null || !setBonus.enabled) continue;
            long pieceCount = armorMap.getOrDefault(setBonus.material, 0L);
            if (pieceCount < setBonus.getNeededCount()) continue;

            for (ConfigSetBonus.Effect effect : setBonus.effects) {
                int strength = setBonus.getStrength(effect, self, armorMap.get(setBonus.material));
                if (strength < 0) continue;
                currentEffects.add(effect.id);
                StatusEffect status = Registry.STATUS_EFFECT.get(Identifier.tryParse(effect.id));
                if(!effect.waitDuration || !self.hasStatusEffect(status)) {
                    self.addStatusEffect(new StatusEffectInstance(
                            status, effect.duration,
                            strength, effect.ambient,
                            effect.showParticles, effect.showIcon));
                }
            }

            armorBuffs[0] += setBonus.protection * pieceCount;
            armorBuffs[1] += setBonus.toughness * pieceCount;
            armorBuffs[2] += setBonus.knockbackResistance * pieceCount / 10f;
        }

        for (String prevEffect : setBonusEffects) {
            if (!currentEffects.contains(prevEffect)) {
                self.removeStatusEffect(Registry.STATUS_EFFECT.get(Identifier.tryParse(prevEffect)));
            }
        }

        Objects.requireNonNull(self.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).setBaseValue(armorBuffs[0]);
        Objects.requireNonNull(self.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).setBaseValue(armorBuffs[1]);
        Objects.requireNonNull(self.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(armorBuffs[2]);

        setBonusEffects.clear();
        setBonusEffects.addAll(currentEffects);
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

    @Override
    public float getAddProtection() {
        return armorBuffs[0] / 4f;
    }

    @Override
    public float getAddToughness() {
        return armorBuffs[1] / 4f;
    }

    @Override
    public float getAddKnockbackResistance() {
        return armorBuffs[2] / 4f;
    }
}
