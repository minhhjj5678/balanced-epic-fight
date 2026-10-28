package com.neojaden.bef.combat;

import com.neojaden.bef.BalancedEpicFight;
import com.neojaden.bef.data.ModifierReloadListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = BalancedEpicFight.MODID)
public class StaminaCostCalculator {
    private static final UUID EVENT_UUID = UUID.fromString("cabb428f-cdd1-48ef-8e5c-961d1c0293db");

    public static float getModifiedStamina(Player player, float amount) {
        float count = AttackCooldownCalculator.countNearbyMonster(player, (mob, owner) -> mob.isAlive() && mob.getTarget() == owner && !ModifierReloadListener.STAMINA_COST.excludedMobs().contains(ForgeRegistries.ENTITY_TYPES.getKey(mob.getType())));
        count = Math.min(count, ModifierReloadListener.STAMINA_COST.maxMobCount());
        if (count <= 1) return amount;

        float modifiedStamina = amount;
        if ("inverse".equalsIgnoreCase(ModifierReloadListener.STAMINA_COST.algorithm())) {
            modifiedStamina /= count;
        } else {
            modifiedStamina = Math.max(modifiedStamina * (1 - (count - 1) * ModifierReloadListener.STAMINA_COST.reductionPerMob()), 0);
        }

        return modifiedStamina;
    }

    @SubscribeEvent
    public static void onPlayerJoined(EntityJoinLevelEvent e) {
        if (e.getEntity() instanceof ServerPlayer player) {
            PlayerPatch<?> playerPatch = EpicFightCapabilities.getPlayerPatch(player);
            if (playerPatch != null) {
                playerPatch.getEventListener().addEventListener(EventType.SKILL_CONSUME_EVENT, EVENT_UUID, (event) -> {
                    Skill.Resource resource = event.getResourceType();
                    if (resource == Skill.Resource.STAMINA) {
                        float amount = event.getAmount();
                        event.setAmount(getModifiedStamina(player, amount));
                    }
                });

            }

        }
    }
}
