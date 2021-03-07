package com.than00ber.productivityenchantments.enchantments.types;

import com.than00ber.productivityenchantments.enchantments.IRightClickEffect;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.GameData;

import java.util.Map;
import java.util.Random;

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
    public void onRightClick(ItemStack stack, int level, Direction facing, World world, BlockPos origin, PlayerEntity player) {
        int radius = (int) Math.pow(2, level);
        Block block = null;

        if (stack.getItem() instanceof BlockItem)
            block = ((BlockItem) stack.getItem()).getBlock();

        for (int x = 0; x < radius; x++)
            for (int y = 0; y < radius; y++)
                for (int z = 0; z < radius; z++)
                    world.setBlockState(origin.add(x, y + 1, z), getBlock(block).getDefaultState(), 2);
    }

    private static Block getBlock(Block block) {
        if (block != null) return block;

        Random r = new Random();
        Map<Block, Item> blockItemMap = GameData.getBlockItemMap();
        for (Block b : blockItemMap.keySet()) {
            if (r.nextInt(100) == 0)
                return b.getBlock();
        }

        return Blocks.DIRT;
    }
}
