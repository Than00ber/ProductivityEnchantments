package com.than00ber.productivityenchantments.enchantments.types;

import com.than00ber.productivityenchantments.Configs;
import com.than00ber.productivityenchantments.enchantments.CarvedVolume;
import com.than00ber.productivityenchantments.enchantments.CarverEnchantmentBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Collections;
import java.util.Set;

import static com.than00ber.productivityenchantments.Configs.CULTIVATION_CARVE_TYPE;
import static com.than00ber.productivityenchantments.ProductivityEnchantments.RegistryEvents.CULTIVATION;

public class CultivationEnchantment extends CarverEnchantmentBase {

    public CultivationEnchantment() {
        super(Rarity.COMMON, ToolType.HOE);
    }

    @Override
    public boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type, Direction direction) {
        return state.getBlock() instanceof CropsBlock && state.get(CropsBlock.AGE).equals(Collections.max(CropsBlock.AGE.getAllowedValues()));
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMaxEffectiveRadius(int level) {
        return 2 + level;
    }

    @Override
    public Set<BlockPos> getRemoveVolume(ItemStack stack, int level, CarverEnchantmentBase enchantment, World world, BlockPos origin) {
        int radius = this.getMaxEffectiveRadius(level);

        CarvedVolume volume = new CarvedVolume(CarvedVolume.Shape.DISC, radius, origin, world)
                .setToolRestrictions(stack, CULTIVATION.getToolType())
                .filterViaCallback(CULTIVATION);

        if (CULTIVATION_CARVE_TYPE.get().equals(Configs.CarveType.CONNECTED))
            volume.filterConnectedRecursively();

        return volume.sortNearestToOrigin().getVolume();
    }
}
