package eu.codedsakura.setbonuses.mixin;

import eu.codedsakura.setbonuses.ducks.IPlayerEntityDuck;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.network.ServerPlayerEntity$1")
public class ServerPlayerEntityInnerClass1Mixin {
    @Shadow ServerPlayerEntity field_29182;

    @Inject(method = "updateState(Lnet/minecraft/screen/ScreenHandler;Lnet/minecraft/util/collection/DefaultedList;Lnet/minecraft/item/ItemStack;[I)V", at = @At("HEAD"))
    public void updateState(ScreenHandler handler, DefaultedList<ItemStack> stacks, ItemStack cursorStack, int[] properties, CallbackInfo ci) {
        if (this.field_29182 != null && handler instanceof PlayerScreenHandler) {
            EquipmentSlot[] slots = new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
            for (int i = 5; i < 9; i++) {
                int finalI = i;
                stacks.get(i).getAttributeModifiers(slots[i-5]).forEach((entityAttribute, entityAttributeModifier) -> {
                    if (entityAttribute == EntityAttributes.GENERIC_ARMOR) entityAttributeModifier = new EntityAttributeModifier(
                            entityAttributeModifier.getId(), entityAttributeModifier.getName(), entityAttributeModifier.getValue() + 12, entityAttributeModifier.getOperation()
                    );
                    stacks.get(finalI).addAttributeModifier(entityAttribute, entityAttributeModifier, slots[finalI -5]);
                });
            }
        }
    }

    @ModifyVariable(method = "updateSlot(Lnet/minecraft/screen/ScreenHandler;ILnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), ordinal = 0)
    public ItemStack updateSlot(ItemStack stack, ScreenHandler handler, int slot) {
        if (slot < 5 || slot > 8) return stack;
        if (this.field_29182 != null && handler instanceof PlayerScreenHandler) {
            EquipmentSlot equipmentSlot = new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}[slot-5];
            IPlayerEntityDuck player = (IPlayerEntityDuck) field_29182;
            ItemStack copy = stack.copy();
            stack.getAttributeModifiers(equipmentSlot).forEach((entityAttribute, entityAttributeModifier) -> {
                if (entityAttribute == EntityAttributes.GENERIC_ARMOR) {
                    entityAttributeModifier = new EntityAttributeModifier(
                            entityAttributeModifier.getId(), entityAttributeModifier.getName(),
                            entityAttributeModifier.getValue() + player.getAddProtection(), entityAttributeModifier.getOperation()
                    );
                } else if (entityAttribute == EntityAttributes.GENERIC_ARMOR_TOUGHNESS) {
                    entityAttributeModifier = new EntityAttributeModifier(
                            entityAttributeModifier.getId(), entityAttributeModifier.getName(),
                            entityAttributeModifier.getValue() + player.getAddToughness(), entityAttributeModifier.getOperation()
                    );
                } else if (entityAttribute == EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE) {
                    entityAttributeModifier = new EntityAttributeModifier(
                            entityAttributeModifier.getId(), entityAttributeModifier.getName(),
                            entityAttributeModifier.getValue() + player.getAddKnockbackResistance(), entityAttributeModifier.getOperation()
                    );
                }
                copy.addAttributeModifier(entityAttribute, entityAttributeModifier, equipmentSlot);
            });
            return copy;
        }
        return stack;
    }
}
