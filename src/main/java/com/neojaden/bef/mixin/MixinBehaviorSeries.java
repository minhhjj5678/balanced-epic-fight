package com.neojaden.bef.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

@Mixin(value = CombatBehaviors.BehaviorSeries.class, remap = false)
public interface MixinBehaviorSeries<T extends yesman.epicfight.world.capabilities.entitypatch.MobPatch<?>> {

    @Accessor("loopFinished")
    boolean getLoopFinished();

    @Accessor("loopFinished")
    void setLoopFinished(boolean value);

    @Accessor("looping")
    boolean isLooping();

    @Accessor("nextBehaviorPointer")
    void setNextBehaviorPointer(int value);
}
