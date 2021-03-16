package com.than00ber.productivityenchantments.enchantments.types;

import com.than00ber.productivityenchantments.enchantments.CarvedVolume;
import com.than00ber.productivityenchantments.enchantments.IRightClickEffect;
import com.than00ber.productivityenchantments.enchantments.IValidatorCallback;
import net.minecraft.block.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.than00ber.productivityenchantments.Configs.GROWING_CROPS_DAMAGE_ITEM;
import static com.than00ber.productivityenchantments.Configs.PLANTING_SEEDS_DAMAGE_ITEM;

public class FertilityEnchantment extends Enchantment implements IRightClickEffect, IValidatorCallback {

    public FertilityEnchantment() {
        super(Rarity.COMMON, EnchantmentType.DIGGER, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND });
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof HoeItem;
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
        return (state.getBlock() == Blocks.FARMLAND && world.getBlockState(pos.up()).getBlock() == Blocks.AIR) || state.getBlock() instanceof CropsBlock;
    }

    @Override
    public ActionResultType onRightClick(ItemStack heldItem, int level, Direction facing, World world, BlockPos origin, PlayerEntity player) {

        if (!player.isSneaking() || !player.isCrouching()) {
            PlayerInventory inventory = player.inventory;
            boolean isInCreative = player.isCreative();

            Block block = world.getBlockState(origin).getBlock();
            IValidatorCallback callback;
            Pair<Integer, ItemStack> pair;

            if (block instanceof CropsBlock) {
                pair = getInventoryItemStack(inventory, Items.BONE_MEAL);
                if (pair == null && isInCreative) pair = new MutablePair<>(-1, new ItemStack(Items.BONE_MEAL));

                callback = new IValidatorCallback() {
                    @Override
                    public boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type, Direction direction) {

                        if (state.getBlock() instanceof CropsBlock) {

                            return state.getBlock() instanceof BeetrootBlock
                                    ? state.get(BeetrootBlock.BEETROOT_AGE) < Collections.max(BeetrootBlock.BEETROOT_AGE.getAllowedValues())
                                    : state.get(CropsBlock.AGE) < Collections.max(CropsBlock.AGE.getAllowedValues());
                        }
                        return false;
                    }
                };
            }
            else {
                pair = getSeedType(inventory);
                if (pair == null && isInCreative) pair = new MutablePair<>(-1, new ItemStack(Items.WHEAT_SEEDS));

                callback = new IValidatorCallback() {
                    @Override
                    public boolean isBlockValid(BlockState state, World world, BlockPos pos, ItemStack stack, ToolType type, Direction direction) {
                        return state.getBlock() == Blocks.FARMLAND && world.getBlockState(pos.up()).getBlock() == Blocks.AIR;
                    }
                };
            }

            if (pair != null) {
                CarvedVolume area = new CarvedVolume(CarvedVolume.Shape.DISC, level + 1, origin, world)
                        .setToolRestrictions(heldItem, ToolType.HOE)
                        .filterViaCallback(callback)
                        .sortNearestToOrigin();

                if (!(block instanceof CropsBlock)) area.shiftBy(0, 1, 0);
                List<BlockPos> surface = new ArrayList<>(area.getVolume());
                AtomicBoolean notBroken = new AtomicBoolean(true);
                int quantityInInv = !isInCreative ? pair.getRight().getCount() : 64;
                Item itemInInv = pair.getRight().getItem();

                if (player instanceof ServerPlayerEntity) {

                    for (int i = 0; i < surface.size() && i < quantityInInv; i++) {

                        if (notBroken.get()) {
                            BlockPos pos = surface.get(i);
                            BlockState current = world.getBlockState(pos);

                            if (!world.isRemote()) {

                                if (itemInInv == Items.BONE_MEAL) {
                                    if (current.getBlock() instanceof CropsBlock)
                                        ((CropsBlock) current.getBlock()).grow(world, pos, current);
                                }
                                else {
                                    Block seed = Block.getBlockFromItem(itemInInv);
                                    world.setBlockState(pos, seed.getDefaultState());
                                }
                            }

                            if (!isInCreative) {
                                inventory.decrStackSize(pair.getLeft(), 1);

                                if (i % 2 == 0) {
                                    boolean fromBoneMeal = itemInInv == Items.BONE_MEAL && GROWING_CROPS_DAMAGE_ITEM.get();
                                    boolean fromSeed = itemInInv != Items.BONE_MEAL && PLANTING_SEEDS_DAMAGE_ITEM.get();

                                    if (fromSeed || fromBoneMeal) heldItem.damageItem(1, player, p -> notBroken.set(false));
                                }
                            }
                        }
                    }
                }

                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.PASS;
    }

    private static final Item[] SEED_TYPE = new Item[] { Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.CARROT, Items.POTATO };

    private static ImmutablePair<Integer, ItemStack> getSeedType(PlayerInventory inventory) {
        for (int i = 0; i < Arrays.stream(SEED_TYPE).count(); i++) {
            ImmutablePair<Integer, ItemStack> pair = getInventoryItemStack(inventory, SEED_TYPE[i]);
            if (pair != null) return pair;
        }
        return null;
    }

    private static ImmutablePair<Integer, ItemStack> getInventoryItemStack(PlayerInventory inventory, Item item) {
        int inSlot = inventory.getSlotFor(new ItemStack(item));
        return inSlot != -1 ? new ImmutablePair<>(inSlot, inventory.getStackInSlot(inSlot)) : null;
    }
}