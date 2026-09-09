package com.neojaden.bef.mixin;

import com.neojaden.bef.combat.AttackCooldownCalculator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

@Mixin(value = CombatBehaviors.class, remap = false)
public class MixinCombatBehaviors<T extends MobPatch<?>> {

    @Shadow
    @Final
    private List<CombatBehaviors.BehaviorSeries<T>> behaviorSeriesList;
    @Shadow
    private int currentBehaviorPointer;

    @Shadow
    @Final
    private T mobpatch;

    @Unique
    private int bef$cooldownUntil;

    @Unique
    private void bef$triggerCooldown() {
        if (this.mobpatch.getOriginal().tickCount >= this.bef$cooldownUntil) {
            this.bef$cooldownUntil = this.mobpatch.getOriginal().tickCount + AttackCooldownCalculator.getCooldownTicks(this.mobpatch);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bef$timerConstructor(CombatBehaviors.Builder<T> builder, T mobpatch, CallbackInfo ci) {
        this.bef$cooldownUntil = mobpatch.getOriginal().tickCount;
    }

    @Inject(method = "selectRandomBehaviorSeries", at = @At("HEAD"), cancellable = true)
    private void bef$blockNewAttack(CallbackInfoReturnable<CombatBehaviors.Behavior<T>> cir) {
        if (this.mobpatch.getOriginal().tickCount < bef$cooldownUntil) {
            cir.setReturnValue(null);
        }
    }

    // Handle one-behavior attack series
    @Inject(method = "selectRandomBehaviorSeries", at = @At("RETURN"))
    private void bef$handleSingleBehaviorSeries(CallbackInfoReturnable<CombatBehaviors.Behavior<T>> cir) {
        if (cir.getReturnValue() != null && this.currentBehaviorPointer < 0) {
            bef$triggerCooldown();
        }
    }

    // Handle completed multi-behavior attack series
    @SuppressWarnings("unchecked")
    @Inject(method = "tryProceed", at = @At("HEAD"), cancellable = true)
    private void bef$handleCompletedAttackSeries(CallbackInfoReturnable<CombatBehaviors.Behavior<T>> cir) {
        if (this.currentBehaviorPointer < 0) return;
        MixinBehaviorSeries<T> currentBehaviorSeries = (MixinBehaviorSeries<T>) this.behaviorSeriesList.get(this.currentBehaviorPointer);
        if (currentBehaviorSeries.getLoopFinished()) {
            currentBehaviorSeries.setLoopFinished(false);
            this.currentBehaviorPointer = -1;
            if (!currentBehaviorSeries.isLooping()) {
                currentBehaviorSeries.setNextBehaviorPointer(0);
            }
            bef$triggerCooldown();
            cir.setReturnValue(null);
        }
    }

    // Handle interrupted/broken attack series
    @Inject(method = "tryProceed", at = @At("RETURN"))
    private void bef$catchComboBreak(CallbackInfoReturnable<CombatBehaviors.Behavior<T>> cir) {
        if (cir.getReturnValue() == null) {
            boolean isTakingDamage = this.mobpatch.getOriginal().hurtTime > 0;
            boolean isAttackMissed = this.mobpatch.getEntityState().inaction();
            int ticksSinceHurt = this.mobpatch.getOriginal().tickCount - this.mobpatch.getOriginal().getLastHurtByMobTimestamp();
            boolean recentlyHurt = (ticksSinceHurt >= 0 && ticksSinceHurt <= 30);

            if (!isTakingDamage && !isAttackMissed && !recentlyHurt) {
                bef$triggerCooldown();
            }
        }
    }
}
