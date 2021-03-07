package com.than00ber.productivityenchantments.enchantments.types;

import com.than00ber.productivityenchantments.CarvedVolume;
import com.than00ber.productivityenchantments.IValidatorCallback;
import com.than00ber.productivityenchantments.enchantments.CarverEnchantmentBase;
import com.than00ber.productivityenchantments.enchantments.IRightClickEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.than00ber.productivityenchantments.ProductivityEnchantments.RegistryEvents.FERTILITY;

public class FertilityEnchantment extends CarverEnchantmentBase implements IRightClickEffect {

    public FertilityEnchantment() {
        super(Rarity.UNCOMMON, ToolType.HOE);
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        if (enchantment instanceof CarverEnchantmentBase)
            return ((CarverEnchantmentBase) enchantment).getToolType().equals(ToolType.HOE);
        return super.canApplyTogether(enchantment);
    }

    @Override
    public boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type) {
        if (state.getBlock() instanceof CropsBlock)
            return !state.get(CropsBlock.AGE).equals(Collections.max(CropsBlock.AGE.getAllowedValues()));
        return (state.getBlock() == Blocks.FARMLAND && world.getBlockState(pos.up()).getBlock() == Blocks.AIR);
    }

    @Override
    public Set<BlockPos> getRemoveVolume(ItemStack stack, int level, CarverEnchantmentBase enchantment, World world, BlockPos origin) {
        Set<BlockPos> block = new HashSet<>();
        block.add(origin);
        return block;
    }

    @Override
    public void onRightClick(ItemStack heldItem, int level, Direction facing, CarverEnchantmentBase enchantment, World world, BlockPos origin, PlayerEntity player) {

        if (!player.isSneaking() || !player.isCrouching()) {
            PlayerInventory inventory = player.inventory;
            int radius = enchantment.getMaxEffectiveRadius(level);
            Block block = world.getBlockState(origin).getBlock();

            CarvedVolume area = new CarvedVolume(CarvedVolume.Shape.DISC, radius, origin, world);
            IValidatorCallback callback;

            if (block instanceof CropsBlock) {
                if (!inventory.hasItemStack(Items.BONE_MEAL.getDefaultInstance()) && !player.isCreative()) return;

                callback = new IValidatorCallback() {
                    @Override
                    public boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type) {
                        return state.getBlock() instanceof CropsBlock && state.get(CropsBlock.AGE) < Collections.max(CropsBlock.AGE.getAllowedValues());
                    }
                };
            }
            else {
                if (!inventory.hasItemStack(Items.WHEAT_SEEDS.getDefaultInstance()) && !player.isCreative()) return;

                callback = new IValidatorCallback() {
                    @Override
                    public boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type) {
                        return state.getBlock() == Blocks.FARMLAND && world.getBlockState(pos.up()).getBlock() == Blocks.AIR;
                    }
                };
            }

            area.setToolRestrictions(heldItem, FERTILITY.getToolType())
                    .filterViaCallback(callback)
                    .sortNearestToOrigin();

            if (!(block instanceof CropsBlock)) area.shiftBy(0, 1, 0);

            AtomicBoolean notBroken = new AtomicBoolean(true);
            List<BlockPos> surface = new ArrayList<>(area.getVolume());

            int inSlot = block instanceof CropsBlock
                    ? inventory.getSlotFor(new ItemStack(Items.BONE_MEAL))
                    : inventory.getSlotFor(new ItemStack(Items.WHEAT_SEEDS));
            int quantityInInv = player.isCreative() ? surface.size() : inventory.getStackInSlot(inSlot).getCount();
            if (!player.isCreative()) inventory.decrStackSize(inSlot, surface.size());

            if (block instanceof CropsBlock) {

                for (int i = 0; i < surface.size() && i < quantityInInv; i++) {

                    if (notBroken.get()) {
                        BlockPos blockPos = surface.get(i);
                        BlockState current = world.getBlockState(blockPos);

                        if (current.getBlock() instanceof CropsBlock)
                            ((CropsBlock) current.getBlock()).grow(world, blockPos, current);
                    }
                    else {
                        return;
                    }
                }
            }
            else {
                BlockState seed = Blocks.WHEAT.getDefaultState();

                for (int i = 0; i < surface.size() && i < quantityInInv; i++) {

                    if (notBroken.get()) {
                        BlockPos blockPos = surface.get(i);
                        world.setBlockState(blockPos, seed);
                        if (i % 2 == 0) heldItem.damageItem(1, player, p -> notBroken.set(false));
                    }
                    else {
                        return;
                    }
                }
            }
        }
    }
}
