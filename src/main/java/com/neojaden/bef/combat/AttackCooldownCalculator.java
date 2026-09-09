package com.neojaden.bef.combat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.phys.AABB;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

import java.util.List;

public class AttackCooldownCalculator {
    public static final int BASE_COOLDOWN_TICKS = 40;
    public static final float TICKS_DECREASE_PER_HEALTH = 0.2f;
    public static final float TICKS_INCREASE_PER_MOB = 8f;

    public static int getCooldownTicks(MobPatch<?> mobPatch) {
        int decrement = (int) (mobPatch.getOriginal().getMaxHealth() * TICKS_DECREASE_PER_HEALTH);
        int increment =  (int) (countNearbyMonster(mobPatch.getOriginal()) * TICKS_INCREASE_PER_MOB);
        return Mth.clamp(BASE_COOLDOWN_TICKS - decrement + increment, 0, 100);
    }

    public static int countNearbyMonster(LivingEntity entity) {
        if (entity.level() instanceof ServerLevel level) {
            AABB boundingBox = entity.getBoundingBox().inflate(8);
            List<Mob> monsters = level.getEntitiesOfClass(Mob.class, boundingBox, e -> e != entity && e.isAlive() && e.getTarget() != entity && (!(entity instanceof Targeting mob) || mob.getTarget() != e));
            return monsters.size();
        }

        return 0;
    }

}
