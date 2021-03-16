package com.than00ber.productivityenchantments.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.HashSet;
import java.util.Set;

public class CarverEnchantmentBase extends Enchantment implements IValidatorCallback {

    private final ToolType tooltype;

    protected CarverEnchantmentBase(Rarity rarity, ToolType type) {
        super(rarity, EnchantmentType.DIGGER, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND });
        tooltype = type;
    }

    public ToolType getToolType() {
        return tooltype;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return super.canApply(stack) && stack.getToolTypes().contains(tooltype);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    public int getMaxEffectiveRadius(int level) {
        return level + 1;
    }

    public Set<BlockPos> getRemoveVolume(ItemStack stack, int level, CarverEnchantmentBase enchantment, World world, BlockPos origin) {
        Set<BlockPos> block = new HashSet<>();
        block.add(origin);
        return block;
    }
}