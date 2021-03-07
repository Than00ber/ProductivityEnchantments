package com.than00ber.productivityenchantments.enchantments;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public interface IRightClickEffect {

    default void onRightClick(ItemStack stack, int level, Direction facing, World world, BlockPos origin, PlayerEntity player) {}

    default void onRightClick(ItemStack stack, int level, Direction facing, CarverEnchantmentBase enchantment, World world, BlockPos origin, PlayerEntity player) {}

    default void performPlacements(World world, PlayerEntity player, ItemStack heldItem, Set<BlockPos> area, BlockState state) {
        AtomicBoolean notBroken = new AtomicBoolean(true);

        for (BlockPos blockPos : area) {

            if (notBroken.get()) {
                world.setBlockState(blockPos, state);
                heldItem.damageItem(1, player, p -> notBroken.set(false));
            }
            else {
                return;
            }
        }
    }
}
