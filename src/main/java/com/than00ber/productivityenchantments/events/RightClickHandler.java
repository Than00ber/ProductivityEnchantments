package com.than00ber.productivityenchantments.events;

import com.than00ber.productivityenchantments.enchantments.IRightClickEffect;
import net.minecraft.block.BlockState;
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

        ActionResultType actionResult = ActionResultType.PASS;

        if (!event.getWorld().isRemote()) {

            for (Enchantment enchantment : enchantments.keySet()) {
                if (actionResult == ActionResultType.SUCCESS) break;

                System.out.println(enchantments.size());

                if (enchantment instanceof IRightClickEffect) {
                    IRightClickEffect iRightClickEffect = (IRightClickEffect) enchantment;
                    int lvl = enchantments.get(enchantment);
                    BlockPos pos = event.getPos();
                    World world = event.getWorld();
                    BlockState state = world.getBlockState(pos);
                    Direction facing = event.getFace();

                    if (iRightClickEffect.isBlockValid(state, world, pos, heldItem, iRightClickEffect.getToolType(), facing)) {
                        actionResult = iRightClickEffect.onRightClick(heldItem, lvl, facing, world, pos, player);
                    }
                }
            }
        }
    }
}
