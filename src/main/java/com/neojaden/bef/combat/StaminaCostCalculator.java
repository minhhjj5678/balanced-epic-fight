package com.neojaden.bef.combat;

import com.neojaden.bef.BalancedEpicFight;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = BalancedEpicFight.MODID)
public class StaminaCostCalculator {
    private static final UUID EVENT_UUID = UUID.fromString("cabb428f-cdd1-48ef-8e5c-961d1c0293db");

    public static float getModifiedStamina(Player player, float amount) {
        float count = AttackCooldownCalculator.countNearbyMonster(player, (mob, owner) -> mob.isAlive() && mob.getTarget() == owner);
        return Mth.clamp(count > 1 ? amount/count : amount, amount/5, amount);
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
