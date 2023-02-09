package arcaneenchantments.arcaneenchantments;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ArrowDamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.UntouchingEnchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class ArcaneEnchantmentsEventHandler {

    private static final Logger LOGGER = LogUtils.getLogger();
    @SubscribeEvent
    public static void pickupItem(EntityItemPickupEvent event) {
        System.out.println("Item picked up!");
        //event.getEntity().giveExperienceLevels(1);
    }
    @SubscribeEvent
    public static void breakBlock(BlockEvent.BreakEvent event) {
        for(DigTool de : ArcaneEnchantments.DIGGABLE_ENCHANTMENTS) {
            de.handleBreakBlock(event);
        }
    }
    @SubscribeEvent
    public static void livingDeathEvent(LivingDeathEvent event) {
        for(WeaponTool wp : ArcaneEnchantments.WEAPON_ENCHANTMENTS) {
            wp.handleEntityEvent(event);
        }
    }


}
