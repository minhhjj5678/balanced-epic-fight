package com.neojaden.bef.combat;

import com.neojaden.bef.BalancedEpicFight;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.IdentifierProvider;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@EventBusSubscriber(modid = BalancedEpicFight.MODID)
public class StaminaCostCalculator {
    private static final IdentifierProvider dummy = IdentifierProvider.constant("bef:stamina_cost_modifier");

    public static float getModifiedStamina(Player player, float amount) {
        float count = AttackCooldownCalculator.countNearbyMonster(player, (mob, owner) -> mob.isAlive() && mob.getTarget() == owner);
        return Mth.clamp(count > 1 ? amount/count : amount, amount/5, amount);
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
