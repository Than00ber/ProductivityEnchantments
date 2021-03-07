package com.than00ber.productivityenchantments;

import com.than00ber.productivityenchantments.enchantments.CarverEnchantmentBase;
import com.than00ber.productivityenchantments.enchantments.types.*;
import com.than00ber.productivityenchantments.events.BlockBreakHandler;
import com.than00ber.productivityenchantments.events.LivingKilledHandler;
import com.than00ber.productivityenchantments.events.RightClickHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod(ProductivityEnchantments.MODID)
public class ProductivityEnchantments {

    public static final String MODID = "productivityenchantments";

    public ProductivityEnchantments() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        MinecraftForge.EVENT_BUS.register(new BlockBreakHandler());
        MinecraftForge.EVENT_BUS.register(new RightClickHandler());
        MinecraftForge.EVENT_BUS.register(new LivingKilledHandler());
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        public static final ChunkEnchantment CHUNK = new ChunkEnchantment();
        public static final CarverEnchantmentBase WOODCUTTING = new WoodcuttingEnchantment();
        public static final CarverEnchantmentBase CLUSTER = new ClusterEnchantment();
        public static final CarverEnchantmentBase DIGGING = new DiggingEnchantment();
        public static final CarverEnchantmentBase CULTIVATION = new CultivationEnchantment();
        public static final CarverEnchantmentBase PLOWING = new PlowingEnchantment();
        public static final CarverEnchantmentBase FERTILITY = new FertilityEnchantment();
        public static final MagnetismEnchantment MAGNETISM = new MagnetismEnchantment();
        public static final TorchingEnchantment TORCHING = new TorchingEnchantment();

        @SubscribeEvent
        public static void onEnchantmentRegistry(final RegistryEvent.Register<Enchantment> event) {
            event.getRegistry().registerAll(
                    CHUNK.setRegistryName(MODID, "chunk"),
                    WOODCUTTING.setRegistryName(MODID, "woodcutting"),
                    CLUSTER.setRegistryName(MODID, "cluster"),
                    DIGGING.setRegistryName(MODID, "digging"),
                    CULTIVATION.setRegistryName(MODID, "cultivation"),
                    PLOWING.setRegistryName(MODID, "plowing"),
                    FERTILITY.setRegistryName(MODID, "fertility"),
                    MAGNETISM.setRegistryName(MODID, "magnetism"),
                    TORCHING.setRegistryName(MODID, "torching")
            );
        }
    }
}