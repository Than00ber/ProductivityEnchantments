package com.than00ber.productivityenchantments.enchantments.types;

import com.than00ber.productivityenchantments.enchantments.CarvedVolume;
import com.than00ber.productivityenchantments.enchantments.CarverEnchantmentBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;

import java.util.Set;

import static com.than00ber.productivityenchantments.ProductivityEnchantments.RegistryEvents.CLUSTER;

public class ClusterEnchantment extends CarverEnchantmentBase {

    public ClusterEnchantment() {
        super(Rarity.RARE, ToolType.PICKAXE);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem;
    }

    @Override
    public int getMaxEffectiveRadius(int level) {
        return 5;
    }

    @Override
    public boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type, Direction direction) {
        boolean isOre = state.isIn(Tags.Blocks.ORES) || state.getBlock() instanceof OreBlock;
        return stack.canHarvestBlock(state) && isOre;
    }

    @Override
    public Set<BlockPos> getRemoveVolume(ItemStack stack, int level, CarverEnchantmentBase enchantment, World world, BlockPos origin) {
        int radius = enchantment.getMaxEffectiveRadius(level);
        BlockState ore = world.getBlockState(origin);

        return new CarvedVolume(CarvedVolume.Shape.SPHERICAL, radius, origin, world)
                .setToolRestrictions(stack, CLUSTER.getToolType())
                .filterViaCallback(CLUSTER)
                .filterBy(ore)
                .filterConnectedRecursively()
                .sortNearestToOrigin()
                .getVolume();
    }
}
