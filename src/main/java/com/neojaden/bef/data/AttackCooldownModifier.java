package com.neojaden.bef.data;

import com.neojaden.bef.BalancedEpicFight;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record AttackCooldownModifier(int baseCooldownTicks, float tickIncreasePerMob, float tickDecreasePerHealth, List<ResourceLocation> excludedMobs) {
    public static final AttackCooldownModifier DEFAULT = new AttackCooldownModifier(40, 0.2f, 8f, List.of());

    public static final String FILE = "attack_cooldown";
    public static final ResourceLocation RL = ResourceLocation.fromNamespaceAndPath(BalancedEpicFight.MODID, FILE);

    public AttackCooldownModifier {
        excludedMobs = excludedMobs == null ? List.of() : List.copyOf(excludedMobs);
    }
}
