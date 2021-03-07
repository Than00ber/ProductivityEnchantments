package com.than00ber.productivityenchantments;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public interface IValidatorCallback {

    default boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type) {
        return defaultCheck(state, stack, type);
    }

    /**
     * Used statically to apply default block validation.
     * Overriding IValidatorCallback#isBlockValid will remove this
     * default check.
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
