package com.than00ber.productivityenchantments.enchantments.types;

import com.than00ber.productivityenchantments.Configs;
import com.than00ber.productivityenchantments.enchantments.CarvedVolume;
import com.than00ber.productivityenchantments.enchantments.IRightClickEffect;
import com.than00ber.productivityenchantments.enchantments.IValidatorCallback;
import net.minecraft.block.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.than00ber.productivityenchantments.Configs.PLOWING_CARVE_TYPE;

public class PlowingEnchantment extends Enchantment implements IRightClickEffect, IValidatorCallback {

    public PlowingEnchantment() {
        super(Rarity.COMMON, EnchantmentType.DIGGER, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public ToolType getToolType() {
        return ToolType.HOE;
    }

    @Override
    public boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type, Direction direction) {
        boolean aboveIsAir = world.getBlockState(pos.up()).getBlock() == Blocks.AIR;
        return aboveIsAir && (state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.GRASS_BLOCK);
    }

    @Override
    public ActionResultType onRightClick(ItemStack stack, int level, Direction facing, World world, BlockPos origin, PlayerEntity player) {

        if (!player.isSneaking() || !player.isCrouching()) {

            IValidatorCallback callback = new IValidatorCallback() {
                @Override
                public boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type, Direction direction) {
                    Block above = world.getBlockState(pos.up()).getBlock();
                    boolean isDirty = state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.GRASS_BLOCK;
                    return isDirty && (above == Blocks.AIR || above instanceof TallGrassBlock || above instanceof DoublePlantBlock);
                }
            };

            CarvedVolume area = new CarvedVolume(CarvedVolume.Shape.DISC, level + 1, origin, world)
                    .setToolRestrictions(stack, this.getToolType())
                    .filterViaCallback(callback);

            if (PLOWING_CARVE_TYPE.get().equals(Configs.CarveType.CONNECTED))
                area.filterConnectedRecursively();

            area.sortNearestToOrigin();

            BlockState state = level == this.getMaxLevel()
                    ? Blocks.FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, Collections.max(FarmlandBlock.MOISTURE.getAllowedValues()))
                    : Blocks.FARMLAND.getDefaultState();

            AtomicBoolean notBroken = new AtomicBoolean(true);

            if (player instanceof ServerPlayerEntity) {

                for (BlockPos blockPos : area.getVolume()) {

                    if (notBroken.get()) {
                        world.setBlockState(blockPos, state);
                        Block above = world.getBlockState(blockPos.up()).getBlock();
                        stack.damageItem(1, player, p -> notBroken.set(false));

                        if (above instanceof TallGrassBlock || above instanceof DoublePlantBlock)
                            world.setBlockState(blockPos.up(), Blocks.AIR.getDefaultState());
                    }
                }
            }

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
