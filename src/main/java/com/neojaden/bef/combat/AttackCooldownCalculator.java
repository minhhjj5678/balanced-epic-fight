package com.neojaden.bef.combat;

import com.neojaden.bef.data.ModifierReloadListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.phys.AABB;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

import java.util.List;
import java.util.function.BiPredicate;

public class AttackCooldownCalculator {

    public static int getCooldownTicks(MobPatch<?> mobPatch) {
        int decrement = (int) (mobPatch.getOriginal().getMaxHealth() * ModifierReloadListener.ATTACK_COOLDOWN.tickDecreasePerHealth());
        int increment =  (int) (countNearbyMonster(mobPatch.getOriginal(), (e, owner) -> e != owner && e.isAlive() && e.getTarget() != owner && (!(owner instanceof Targeting mob) || mob.getTarget() != e)) * ModifierReloadListener.ATTACK_COOLDOWN.tickIncreasePerMob());
        return Mth.clamp(ModifierReloadListener.ATTACK_COOLDOWN.baseCooldownTicks() - decrement + increment, 0, 100);
    }

    public static int countNearbyMonster(LivingEntity entity, BiPredicate<Mob, LivingEntity> condition) {
        if (entity.level() instanceof ServerLevel level) {
            AABB boundingBox = entity.getBoundingBox().inflate(8);
            List<Mob> monsters = level.getEntitiesOfClass(Mob.class, boundingBox, e -> condition.test(e, entity));
            return monsters.size();
        }
        return 0;
    }
}
