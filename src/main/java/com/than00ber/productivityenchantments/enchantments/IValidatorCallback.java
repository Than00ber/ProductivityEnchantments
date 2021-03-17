package com.than00ber.productivityenchantments.enchantments;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public interface IValidatorCallback {

    default ToolType getToolType() {
        return null;
    }

    default boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type, Direction direction) {
        return defaultCheck(state, stack, type);
    }

    /**
     * Used statically to perform default block validation.
     * Overriding IValidatorCallback#isBlockValid will remove this
     * default check but can still be applied when called.
     *
     * @param state target state
     * @param stack item used to break block
     * @param type type of item used
     * @return whether has effect
     */
    static boolean defaultCheck(BlockState state, ItemStack stack, ToolType type) {
        return state.isToolEffective(type) && stack.canHarvestBlock(state);
    }
}
