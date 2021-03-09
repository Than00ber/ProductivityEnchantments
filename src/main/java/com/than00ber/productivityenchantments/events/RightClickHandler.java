package com.than00ber.productivityenchantments.events;

import com.than00ber.productivityenchantments.enchantments.CarverEnchantmentBase;
import com.than00ber.productivityenchantments.enchantments.IRightClickEffect;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

public class RightClickHandler {

    @SubscribeEvent
    public void onRightClickEvent(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        ItemStack heldItem = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(heldItem);

        boolean hasPerformedCarvingAction = false;
        for (Enchantment enchantment : enchantments.keySet()) {
            if (hasPerformedCarvingAction) return;

            if (enchantment instanceof IRightClickEffect) {
                IRightClickEffect rce = (IRightClickEffect) enchantment;
                ActionResultType actionResult = ActionResultType.PASS;
                int lvl = enchantments.get(enchantment);
                BlockPos pos = event.getPos();
                World world = event.getWorld();
                Direction facing = event.getFace();

                if (enchantment instanceof CarverEnchantmentBase) {
                    CarverEnchantmentBase ceb = ((CarverEnchantmentBase) enchantment);

                    if (ceb.isBlockValid(world.getBlockState(pos), world, pos, heldItem, ceb.getToolType()))
                        actionResult = rce.onRightClick(heldItem, lvl, facing, ceb, world, pos, player);
                }
                else {
                    actionResult = rce.onRightClick(heldItem, lvl, facing, world, pos, player);
                }

                if (actionResult == ActionResultType.SUCCESS) {
                    hasPerformedCarvingAction = true;
                    player.swingArm(Hand.MAIN_HAND);
                }
            }
        }
    }
}
