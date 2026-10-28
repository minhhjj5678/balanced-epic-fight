package com.neojaden.bef.data;

import com.neojaden.bef.BalancedEpicFight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Set;

public record StaminaCostModifier(String algorithm, int maxMobCount, float reductionPerMob, Set<ResourceLocation> excludedMobs) {
    public static final String FILE = "stamina_cost";
    public static final ResourceLocation RL = ResourceLocation.fromNamespaceAndPath(BalancedEpicFight.MODID, FILE);
    public static final StaminaCostModifier DEFAULT = new StaminaCostModifier("inverse", 5, 0.15f, Set.of());

    public StaminaCostModifier {
        algorithm = "linear".equalsIgnoreCase(algorithm) ? "linear" : "inverse";
        maxMobCount = Math.max(maxMobCount, 1);
        reductionPerMob = Mth.clamp(reductionPerMob, 0f, 1f);
        excludedMobs = excludedMobs == null ? Set.of() : Set.copyOf(excludedMobs);
    }
}