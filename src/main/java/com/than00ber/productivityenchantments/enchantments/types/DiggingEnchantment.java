package com.than00ber.productivityenchantments.enchantments.types;

import com.than00ber.productivityenchantments.Configs;
import com.than00ber.productivityenchantments.enchantments.CarvedVolume;
import com.than00ber.productivityenchantments.enchantments.CarverEnchantmentBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Set;

import static com.than00ber.productivityenchantments.Configs.DIGGING_CARVE_TYPE;
import static com.than00ber.productivityenchantments.ProductivityEnchantments.RegistryEvents.CLUSTER;
import static com.than00ber.productivityenchantments.ProductivityEnchantments.RegistryEvents.DIGGING;

public class DiggingEnchantment extends CarverEnchantmentBase {

    public DiggingEnchantment() {
        super(Rarity.UNCOMMON, ToolType.SHOVEL);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem || stack.getItem() instanceof ShovelItem;
    }

    @Override
    public boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type, Direction direction) {

        if (stack.getItem() instanceof ShovelItem) {
            return stack.canHarvestBlock(state) || state.isToolEffective(type);
        }
        else if (stack.getItem() instanceof PickaxeItem) {
            boolean isClusterSpecific = CLUSTER.isBlockValid(state, world, pos, stack, type, direction);
            return !isClusterSpecific && stack.canHarvestBlock(state) && state.getBlock() != Blocks.BEDROCK;
        }

        return false;
    }

    @Override
    public Set<BlockPos> getRemoveVolume(ItemStack stack, int level, CarverEnchantmentBase enchantment, World world, BlockPos origin) {
        int radius = enchantment.getMaxEffectiveRadius(level);

        CarvedVolume area = new CarvedVolume(CarvedVolume.Shape.SPHERICAL, radius, origin, world)
                .setToolRestrictions(stack, enchantment.getToolType())
                .filterViaCallback(DIGGING);

        if (!DIGGING_CARVE_TYPE.get().equals(Configs.CarveType.CONNECTED))
            area.filterConnectedRecursively();

        return area.sortNearestToOrigin().getVolume();
    }
}
