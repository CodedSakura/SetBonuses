package eu.codedsakura.setbonuses.mixin;

import eu.codedsakura.setbonuses.EnchantmentFactory;
import eu.codedsakura.setbonuses.IPlayerEnchantmentToggle;
import eu.codedsakura.setbonuses.SetBonuses;
import eu.codedsakura.setbonuses.VirtualEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {

    @Inject(method = "internalOnSlotClick", at = @At("TAIL"))
    private void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ScreenHandler self = (ScreenHandler) (Object) this;

        if (self instanceof PlayerScreenHandler) {
            if (slotIndex >= 5 && slotIndex <= 8 && actionType == SlotActionType.CLONE) {
                ItemStack stack = self.getSlot(slotIndex).getStack();
                if (!stack.isEmpty() && stack.hasEnchantments()) {
                    for (VirtualEnchantment enchant : EnchantmentFactory.enchantments) {
                        if (enchant.enchant.toggleable && enchant.enchant.effects.length != 0
                                && EnchantmentHelper.getLevel(enchant, stack) > 0) {
                            SetBonuses.logger.info("toggle {}", enchant.enchant.id);
                            ((IPlayerEnchantmentToggle) player).toggle(enchant.enchant.id);
                        }
                    }
                }
            }
        }
    }
}
