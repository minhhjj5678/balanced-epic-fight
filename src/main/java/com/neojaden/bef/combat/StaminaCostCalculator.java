package com.neojaden.bef.combat;

import com.neojaden.bef.BalancedEpicFight;
import com.neojaden.bef.data.ModifierReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.IdentifierProvider;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.UUID;

@EventBusSubscriber(modid = BalancedEpicFight.MODID)
public class StaminaCostCalculator {
    private static final IdentifierProvider dummy = IdentifierProvider.constant("bef:stamina_cost_modifier");

    public static float getModifiedStamina(Player player, float amount) {
        float count = AttackCooldownCalculator.countNearbyMonster(player, (mob, owner) -> mob.isAlive() && mob.getTarget() == owner && !ModifierReloadListener.STAMINA_COST.excludedMobs().contains(BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType())));
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
                playerPatch.getEventListener().registerEvent(EpicFightEventHooks.Player.CONSUME_SKILL, (event) -> {
                    Skill.Resource resource = event.getResourceType();
                    if (resource == Skill.Resource.STAMINA) {
                        float amount = event.getAmount();
                        event.setAmount(getModifiedStamina(player, amount));
                    }
                }, dummy);

            }

        }
    }
}