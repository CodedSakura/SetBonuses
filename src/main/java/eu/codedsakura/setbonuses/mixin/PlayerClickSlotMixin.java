package eu.codedsakura.setbonuses.mixin;

import eu.codedsakura.setbonuses.SetBonuses;
import eu.codedsakura.setbonuses.mixin.accessors.ScreenHandlerAccessor;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class PlayerClickSlotMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onClickSlot", at = @At("HEAD"))
    private void onClickSlot(ClickSlotC2SPacket packet, CallbackInfo ci) {
        if (player.currentScreenHandler.syncId == packet.getSyncId()) {
            if (player.currentScreenHandler instanceof PlayerScreenHandler) {
                log(packet);
            }
        }
    }

    private void log(ClickSlotC2SPacket packet) {
        if (packet.getSlot() >= 5 && packet.getSlot() <= 8) {
            SetBonuses.logger.info("slot {} : {}", packet.getSlot(), new EquipmentSlot[] {
                    EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
            }[packet.getSlot() - 5]);
        }
    }
}
