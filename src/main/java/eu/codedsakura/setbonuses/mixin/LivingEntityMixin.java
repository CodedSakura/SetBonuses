package eu.codedsakura.setbonuses.mixin;

import eu.codedsakura.setbonuses.ducks.IPlayerEntityDuck;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "getArmor", at = @At("RETURN"), cancellable = true)
    public void getArmor(CallbackInfoReturnable<Integer> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof PlayerEntity) {
            IPlayerEntityDuck player = (IPlayerEntityDuck) self;
            cir.setReturnValue(MathHelper.floor(cir.getReturnValue() + player.getAddProtection()));
        }
    }

    @ModifyArgs(method = "applyArmorToDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DamageUtil;getDamageLeft(FFF)F"))
    public void applyArmorToDamage(Args args) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof PlayerEntity) {
            IPlayerEntityDuck player = (IPlayerEntityDuck) self;
//            args.set(1, (float) args.get(1) + player.getAddProtection());
            args.set(2, (float) args.get(2) + player.getAddToughness());
        }
    }

    @ModifyVariable(method = "takeKnockback", at = @At("HEAD"), ordinal = 0)
    private double takeKnockback(double strength) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof PlayerEntity) {
            IPlayerEntityDuck player = (IPlayerEntityDuck) self;
            return strength * (1f - player.getAddKnockbackResistance());
        }
        return strength;
    }
}
