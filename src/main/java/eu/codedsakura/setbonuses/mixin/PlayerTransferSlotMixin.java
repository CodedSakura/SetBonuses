package eu.codedsakura.setbonuses.mixin;

import eu.codedsakura.setbonuses.SetBonuses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerTransferSlotMixin {

    @Inject(method = "transferSlot", at = @At("HEAD"))
    public void transferSlot(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) {
        Slot slot = ((PlayerScreenHandler) (Object) this).slots.get(index);
        ItemStack itemStack2 = slot.getStack();
        SetBonuses.logger.info("[Set Bonus] /transfer/ {} {} {}", index, slot, itemStack2);
    }
}
