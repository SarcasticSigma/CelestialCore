package com.xiaoyue.celestial_core.events;

import com.xiaoyue.celestial_core.content.generic.PlayerFlagData;
import com.xiaoyue.celestial_core.register.CCAttributes;
import com.xiaoyue.celestial_core.register.CCEffects;
import com.xiaoyue.celestial_core.register.CCItems;
import com.xiaoyue.celestial_core.utils.EntityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.xiaoyue.celestial_core.CelestialCore.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CCGeneralEventHandler {

	@SubscribeEvent
	public static void onLivingHeal(LivingHealEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player player) {
			double re = player.getAttributeValue(CCAttributes.REPLY_POWER.get());
			event.setAmount((float) (event.getAmount() * re));
		}
	}

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof AbstractArrow arrow) {
			if (arrow.getOwner() instanceof Player player) {
				double as = player.getAttributeValue(CCAttributes.ARROW_SPEED.get());
				double ak = player.getAttributeValue(CCAttributes.ARROW_KNOCK.get());
				if (!arrow.getTags().contains("arrow_speed")) {
					arrow.setDeltaMovement(arrow.getDeltaMovement().scale(as));
					arrow.addTag("arrow_speed");
				}
				arrow.setKnockback(arrow.getKnockback() + (int) ak);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingTeleport(EntityTeleportEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof LivingEntity living) {
			if (living.hasEffect(CCEffects.ROTTEN_CURSE.get())) {
				event.setCanceled(true);
			}
		}
	}

	public static String NETHER_STAGE = "nether_stage";

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();
		DamageSource source = event.getSource();
		Entity attacker = source.getEntity();
		if (attacker instanceof Player player) {
			var data = PlayerFlagData.HOLDER.get(player);
			if (data.hasFlag(NETHER_STAGE)) {
				if (entity instanceof Husk husk) {
					if (0.05 > entity.getRandom().nextDouble()) {
						husk.spawnAtLocation(CCItems.LIGHT_FRAGMENT.get());
					}
				}

				if (entity instanceof Stray stray) {
					if (0.05 > entity.getRandom().nextDouble()) {
						stray.spawnAtLocation(CCItems.MIDNIGHT_FRAGMENT.get());
					}
				}
			}

			if (entity instanceof WitherBoss witherBoss) {
				if (!data.hasFlag(NETHER_STAGE)) {
					data.addFlag(NETHER_STAGE);
				}

				if (EntityUtils.getBeneficialEffect(player) > 12) {
					witherBoss.spawnAtLocation(CCItems.PURE_NETHER_STAR.get());
				}
			}

			if (entity instanceof Blaze blaze) {
				if (0.08 > entity.getRandom().nextDouble()) {
					blaze.spawnAtLocation(CCItems.FIRE_ESSENCE.get());
				}
			}

			if (entity instanceof Guardian guardian) {
				if (0.08 > entity.getRandom().nextDouble()) {
					guardian.spawnAtLocation(CCItems.OCEAN_ESSENCE.get());
				}
			}

			if (entity instanceof Phantom phantom) {
				if (620 < phantom.getY()) {
					phantom.spawnAtLocation(CCItems.SOARING_WINGS.get());
				}
			}

			if (entity instanceof Warden warden) {
				if (0.5 > entity.getRandom().nextDouble()) {
					warden.spawnAtLocation(CCItems.WARDEN_SCLERITE.get());
				}
			}
		}

		if (entity instanceof Shulker shulker) {
			if (source.is(DamageTypeTags.IS_EXPLOSION)) {
				if (0.5 > entity.getRandom().nextDouble()) {
					shulker.spawnAtLocation(CCItems.SHULKER_SCRAP.get());
				}
			}
		}

		if (entity.getMaxHealth() > 100) {
			if (source.is(DamageTypes.WITHER)) {
				if (0.6 > entity.getRandom().nextDouble()) {
					entity.spawnAtLocation(CCItems.DEATH_ESSENCE.get());
				}
			}
		}

		if (entity instanceof Pillager pillager) {
			if (attacker instanceof Creeper creeper) {
				if (creeper.isPowered()) {
					pillager.spawnAtLocation(CCItems.HEART_FRAGMENT.get());
				}
			}
		}
	}
}
