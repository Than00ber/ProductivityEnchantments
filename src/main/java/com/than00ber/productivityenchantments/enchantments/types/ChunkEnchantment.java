package com.than00ber.productivityenchantments.enchantments.types;

import com.than00ber.productivityenchantments.enchantments.CarvedVolume;
import com.than00ber.productivityenchantments.enchantments.IRightClickEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChunkEnchantment extends Enchantment implements IRightClickEffect {

    public ChunkEnchantment() {
        super(Rarity.RARE, EnchantmentType.DIGGER, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND });
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() == Items.STICK || stack.getItem() instanceof BlockItem;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public ActionResultType onRightClick(ItemStack stack, int level, Direction facing, World world, BlockPos origin, PlayerEntity player) {

        if (player instanceof ServerPlayerEntity) {
            int radius = (int) Math.pow(2, level);
            BlockState state = stack.getItem() instanceof BlockItem
                    ? ((BlockItem) stack.getItem()).getBlock().getDefaultState()
                    : Blocks.DIRT.getBlock().getDefaultState();

            CarvedVolume volume = new CarvedVolume(CarvedVolume.Shape.DISC, radius, origin, world);
            for (BlockPos pos : volume.getVolume()) world.setBlockState(pos, state);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
