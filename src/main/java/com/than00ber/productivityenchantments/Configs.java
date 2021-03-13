package com.than00ber.productivityenchantments;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Configs {

    public static ForgeConfigSpec CONFIG_SPEC;
    public static Configs CONFIGS;

    public static ForgeConfigSpec.BooleanValue PLANTING_SEEDS_DAMAGE_ITEM;
    public static ForgeConfigSpec.BooleanValue GROWING_CROPS_DAMAGE_ITEM;
    public static ForgeConfigSpec.BooleanValue PLACING_TORCH_DAMAGE_ITEM;

    public static ForgeConfigSpec.EnumValue<CarveType> WOODCUTTING_CARVE_TYPE;
    public static ForgeConfigSpec.EnumValue<CarveType> DIGGING_CARVE_TYPE;
    public static ForgeConfigSpec.EnumValue<CarveType> CULTIVATION_CARVE_TYPE;
    public static ForgeConfigSpec.EnumValue<CarveType> PLOWING_CARVE_TYPE;
    public static ForgeConfigSpec.EnumValue<CarveType> FERTILITY_CARVE_TYPE;

    public Configs(ForgeConfigSpec.Builder builder) {

        PLANTING_SEEDS_DAMAGE_ITEM = builder
                .comment("Determines whether hoe takes 1/2 of damage per seed planted with Fertility.")
                .define("planting_seeds_damage_item", true);
        GROWING_CROPS_DAMAGE_ITEM = builder
                .comment("Determines whether hoe takes 1/2 of damage per bone meal when attempting to grow crops.")
                .define("growing_crops_damage_item", false);
        PLACING_TORCH_DAMAGE_ITEM = builder
                .comment("Determines whether placing torches deals 1/2 of damage to the item.")
                .define("placing_torch_damage_item", false);

        WOODCUTTING_CARVE_TYPE = builder
                .comment("Change whether Woodcutting breaks all blocks within effective radius or only those connected.")
                .comment("This does not apply to log-type blocks, it will continue to cut down entire trees.")
                .defineEnum("woodcutting_carve_type", CarveType.CONNECTED, CarveType.values());
        DIGGING_CARVE_TYPE = builder
                .comment("Change whether Digging breaks all blocks within effective radius or only those connected.")
                .defineEnum("digging_carve_type", CarveType.CONNECTED, CarveType.values());
        CULTIVATION_CARVE_TYPE = builder
                .comment("Change whether Cultivation breaks all crops within effective radius or only those connected.")
                .defineEnum("digging_carve_type", CarveType.ALL, CarveType.values());
        PLOWING_CARVE_TYPE = builder
                .comment("Change whether Cultivation turns all dirt blocks into farmland within effective radius or only those connected.")
                .defineEnum("plowing_carve_type", CarveType.CONNECTED, CarveType.values());
        FERTILITY_CARVE_TYPE = builder
                .comment("Change whether Fertility plants seeds within connected farmland or all within effective radius.")
                .defineEnum("fertility_carve_type", CarveType.CONNECTED, CarveType.values());
    }

    static {
        Pair<Configs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Configs::new);
        CONFIG_SPEC = pair.getRight();
        CONFIGS = pair.getLeft();
    }

    public enum CarveType {
        CONNECTED, ALL
    }
}
