package com.than00ber.productivityenchantments;

import net.minecraft.util.text.TranslationTextComponent;
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

    public Configs(ForgeConfigSpec.Builder forgeConfigBuilder) {
        ConfigBuilder builder = new ConfigBuilder(forgeConfigBuilder);

        PLANTING_SEEDS_DAMAGE_ITEM = builder.defineBoolean("planting_seeds_damage_item", true);
        GROWING_CROPS_DAMAGE_ITEM = builder.defineBoolean("growing_seeds_damage_item", false);
        PLACING_TORCH_DAMAGE_ITEM = builder.defineBoolean("placing_torch_damage_item", false);

        WOODCUTTING_CARVE_TYPE = builder.defineEnum("woodcutting_carve_type", CarveType.CONNECTED, CarveType.values());
        DIGGING_CARVE_TYPE = builder.defineEnum("digging_carve_type", CarveType.CONNECTED, CarveType.values());
        CULTIVATION_CARVE_TYPE = builder.defineEnum("cultivation_carve_type", CarveType.ALL, CarveType.values());
        PLOWING_CARVE_TYPE = builder.defineEnum("plowing_carve_type", CarveType.CONNECTED, CarveType.values());
        FERTILITY_CARVE_TYPE = builder.defineEnum("fertility_carve_type", CarveType.CONNECTED, CarveType.values());
    }

    static {
        Pair<Configs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Configs::new);
        CONFIG_SPEC = pair.getRight();
        CONFIGS = pair.getLeft();
    }

    private static class ConfigBuilder {

        private static ForgeConfigSpec.Builder BUILDER;

        private ConfigBuilder(ForgeConfigSpec.Builder builder) {
            BUILDER = builder;
        }

        public ForgeConfigSpec.BooleanValue defineBoolean(String key, boolean defaultValue) {
            return BUILDER.comment(fromConfigKey(key), noteFromConfigKey(key)).define(key, defaultValue);
        }

        public ForgeConfigSpec.IntValue defineRange(String key, int defaultValue, int min, int max) {
            return BUILDER.comment(fromConfigKey(key), noteFromConfigKey(key)).defineInRange(key, defaultValue, min, max);
        }

        public ForgeConfigSpec.DoubleValue defineRange(String key, double defaultValue, double min, double max) {
            return BUILDER.comment(fromConfigKey(key), noteFromConfigKey(key)).defineInRange(key, defaultValue, min, max);
        }

        @SafeVarargs
        public final <V extends Enum<V>> ForgeConfigSpec.EnumValue<V> defineEnum(String key, V defaultValue, V... enums) {
            return BUILDER.comment(fromConfigKey(key), noteFromConfigKey(key)).defineEnum(key, defaultValue, enums);
        }

        /**
         * Gets translation component from registry as:
         * config.<key>
         *
         * @param key registry key
         * @return translation component
         */
        private static String fromConfigKey(String key) {
            return new TranslationTextComponent("config." + key).getUnformattedComponentText();
        }

        /**
         * Gets translation component note from registry as
         * config.<key>.note
         *
         * @param key registry key
         * @return translation component
         */
        private static String noteFromConfigKey(String key) {
            return fromConfigKey("prefix.note") + ": " + fromConfigKey(key + ".note");
        }
    }

    public enum CarveType {
        CONNECTED, ALL
    }
}
