package com.than00ber.productivityenchantments.events;

import com.than00ber.productivityenchantments.enchantments.types.MagnetismEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

public class LivingKilledHandler {

    @SubscribeEvent
    public void onLivingDropsEvent(LivingDropsEvent event) {
        Entity source = event.getSource().getTrueSource();

        if (source instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source;

            if (!player.isCreative()) {
                ItemStack heldItem = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(heldItem);

                for (Enchantment enchantment : enchantments.keySet()) {

                    if (enchantment instanceof MagnetismEnchantment) {
                        BlockPos pos = source.getPosition();
                        World world = ((ServerPlayerEntity) source).getServerWorld();

                        for (ItemEntity drop : event.getDrops())
                            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), drop.getItem());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingExperienceDropEvent(LivingExperienceDropEvent event) {
        Entity source = event.getAttackingPlayer();

        if (source instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source;

            if (!player.isCreative()) {
                ItemStack heldItem = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(heldItem);

                for (Enchantment enchantment : enchantments.keySet()) {

                    if (enchantment instanceof MagnetismEnchantment) {
                        BlockPos pos = source.getPosition();
                        World world = ((ServerPlayerEntity) source).getServerWorld();
                        int exp = event.getDroppedExperience();
                        world.addEntity(new ExperienceOrbEntity(world, pos.getX(), pos.getY(), pos.getZ(), exp));
                        event.setDroppedExperience(0);
                    }
                }
            }
        }
    }
}
