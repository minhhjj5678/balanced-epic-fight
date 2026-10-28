package com.neojaden.bef.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ModifierReloadListener extends SimpleJsonResourceReloadListener {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String FOLDER = "modifiers";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();

    public static AttackCooldownModifier ATTACK_COOLDOWN = AttackCooldownModifier.DEFAULT;
    public static StaminaCostModifier STAMINA_COST = StaminaCostModifier.DEFAULT;

    public ModifierReloadListener() {
        super(GSON, FOLDER);
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        ATTACK_COOLDOWN = AttackCooldownModifier.DEFAULT;
        STAMINA_COST = StaminaCostModifier.DEFAULT;

        jsonMap.forEach((key, jsonElement) -> {
            try {
                if (key.equals(AttackCooldownModifier.RL)) {
                    ATTACK_COOLDOWN = GSON.fromJson(jsonElement, AttackCooldownModifier.class);
                } else if (key.equals(StaminaCostModifier.RL)) {
                    STAMINA_COST = GSON.fromJson(jsonElement, StaminaCostModifier.class);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to parse modifier file: {}", key, e);
            }
        });
    }
}
