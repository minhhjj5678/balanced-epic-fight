package com.neojaden.bef.data;

import com.neojaden.bef.BalancedEpicFight;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public record AttackCooldownModifier(int baseCooldownTicks, float tickIncreasePerMob, float tickDecreasePerHealth, int maxCooldownTicks, Set<ResourceLocation> excludedMobs) {
    public static final AttackCooldownModifier DEFAULT = new AttackCooldownModifier(40, 8f, 0.2f, 100, Set.of());

    public static final String FILE = "attack_cooldown";
    public static final ResourceLocation RL = ResourceLocation.fromNamespaceAndPath(BalancedEpicFight.MODID, FILE);

    public AttackCooldownModifier {
        baseCooldownTicks = Math.max(0, baseCooldownTicks);
        maxCooldownTicks = Math.max(0, maxCooldownTicks);
        excludedMobs = excludedMobs == null ? Set.of() : Set.copyOf(excludedMobs);
    }
}
