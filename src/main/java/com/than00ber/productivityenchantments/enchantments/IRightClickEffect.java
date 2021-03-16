package com.than00ber.productivityenchantments.enchantments;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRightClickEffect extends IValidatorCallback {

    default ActionResultType onRightClick(ItemStack stack, int level, Direction facing, World world, BlockPos origin, PlayerEntity player) {
        return ActionResultType.PASS;
    }
}
