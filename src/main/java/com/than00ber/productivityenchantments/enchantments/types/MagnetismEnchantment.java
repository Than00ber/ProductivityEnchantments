package com.than00ber.productivityenchantments.enchantments.types;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;

public class MagnetismEnchantment extends Enchantment {

    public MagnetismEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentType.DIGGER, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND });
    }

    @Override
    public boolean canApply(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof PickaxeItem
                || item instanceof ShovelItem
                || item instanceof SwordItem
                || item instanceof BowItem
                || item instanceof CrossbowItem;
    }
}
