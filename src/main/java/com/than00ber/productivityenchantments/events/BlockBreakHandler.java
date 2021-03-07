package com.than00ber.productivityenchantments.events;

import com.than00ber.productivityenchantments.enchantments.CarverEnchantmentBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.than00ber.productivityenchantments.ProductivityEnchantments.RegistryEvents.MAGNETISM;

public class BlockBreakHandler {

    @SubscribeEvent
    public void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack heldItem = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(heldItem);

        boolean hasPerformedCarvingAction = false;
        for (Enchantment enchantment : enchantments.keySet()) {
            if (hasPerformedCarvingAction) return;

            if (!player.isSneaking() || !player.isCrouching()) {

                if (enchantment instanceof CarverEnchantmentBase) {
                    CarverEnchantmentBase ceb = (CarverEnchantmentBase) enchantment;
                    BlockPos pos = event.getPos();
                    World world = (World) event.getWorld();
                    BlockState state = world.getBlockState(pos);

                    if (ceb.isBlockValid(state, world, pos, heldItem, ceb.getToolType())) {
                        int lvl = enchantments.get(ceb);
                        Block block = state.getBlock();

                        Set<BlockPos> cluster = ceb.getRemoveVolume(heldItem, lvl, ceb, world, pos);
                        AtomicBoolean notBroken = new AtomicBoolean(true);
                        boolean hasMagnetism = enchantments.get(MAGNETISM) != null;

                        for (BlockPos blockPos : cluster) {

                            if (notBroken.get()) {

                                if (!player.isCreative()) {
                                    BlockPos dropPos = hasMagnetism ? player.getPosition() : blockPos;
                                    TileEntity te = world.getTileEntity(pos);
                                    block.harvestBlock(world, player, dropPos, state, te, new ItemStack(block.asItem()));
                                }

                                hasPerformedCarvingAction = true;
                                world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                                heldItem.damageItem(1, player, p -> notBroken.set(false));
                            }
                        }
                    }
                }
            }
        }
    }
}
