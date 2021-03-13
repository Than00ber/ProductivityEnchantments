package com.than00ber.productivityenchantments.enchantments.types;

import com.than00ber.productivityenchantments.enchantments.IRightClickEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.than00ber.productivityenchantments.Configs.PLACING_TORCH_DAMAGE_ITEM;

public class TorchingEnchantment extends Enchantment implements IRightClickEffect {

    public TorchingEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentType.DIGGER, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND });
    }

    @Override
    public boolean canApply(@SuppressWarnings("NullableProblems") ItemStack stack) {
        return super.canApply(stack)
                || (
                        stack.getItem() instanceof PickaxeItem ||
                        stack.getItem() instanceof AxeItem ||
                        stack.getItem() instanceof SwordItem
                );
    }

    @Override
    public ActionResultType onRightClick(ItemStack stack, int level, Direction facing, World world, BlockPos origin, PlayerEntity player) {
        PlayerInventory inventory = player.inventory;

        if (inventory.hasItemStack(new ItemStack(Items.TORCH)) || player.isCreative()) {
            BlockPos current = origin.offset(facing);
            BlockState state = world.getBlockState(current);
            boolean upDown = facing.equals(Direction.UP) || facing.equals(Direction.DOWN);

            if (state.getBlock() == Blocks.AIR) {
                BlockState torch = null;

                if (Blocks.TORCH.isValidPosition(state, world, current) && upDown) {
                    torch = Blocks.TORCH.getDefaultState();
                }
                else if (!upDown) {
                    BlockState wallTorch = Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, facing);

                    if (Blocks.WALL_TORCH.isValidPosition(wallTorch, world, current))
                        torch = wallTorch;
                }

                if (torch != null) {
                    player.swingArm(Hand.MAIN_HAND);
                    world.setBlockState(current, torch);

                    if (!player.isCreative()) {
                        int inSlot = inventory.getSlotFor(new ItemStack(Items.TORCH));
                        inventory.decrStackSize(inSlot, 1);

                        if (world.rand.nextInt(2) == 0 && PLACING_TORCH_DAMAGE_ITEM.get())
                            stack.damageItem(1, player, p -> {});
                    }

                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }
}
