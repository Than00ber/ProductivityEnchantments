package com.than00ber.productivityenchantments.enchantments;

import com.than00ber.productivityenchantments.CarvedVolume;
import com.than00ber.productivityenchantments.IValidatorCallback;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Set;

public class CarverEnchantmentBase extends Enchantment implements IValidatorCallback {

    protected final ToolType TOOL_TYPE;

    protected CarverEnchantmentBase(Rarity rarity, ToolType type) {
        super(rarity, EnchantmentType.DIGGER, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND });
        this.TOOL_TYPE = type;
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return super.canApplyTogether(enchantment) && !(enchantment instanceof CarverEnchantmentBase);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getToolTypes().contains(TOOL_TYPE);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    public ToolType getToolType() {
        return this.TOOL_TYPE;
    }

    public int getMaxEffectiveRadius(int level) {
        return level + 1;
    }

    public Set<BlockPos> getRemoveVolume(ItemStack stack, int level, CarverEnchantmentBase enchantment, World world, BlockPos origin) {
        int radius = enchantment.getMaxEffectiveRadius(level);
        return new CarvedVolume(CarvedVolume.Shape.SPHERICAL, radius, origin, world)
                .setToolRestrictions(stack, enchantment.getToolType())
                .filterConnectedRecursively()
                .sortNearestToOrigin()
                .getVolume();
    }
}