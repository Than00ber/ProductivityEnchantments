package com.than00ber.productivityenchantments;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Configs {

    public static ForgeConfigSpec CONFIG_SPEC;
    public static Configs CONFIGS;

    // value ranges
    public static ForgeConfigSpec.IntValue MAX_SNOW_DEPTH;
    public static ForgeConfigSpec.IntValue SNOW_LIGHT_MELTING_THRESHOLD_BLOCK;
    public static ForgeConfigSpec.IntValue SNOW_LIGHT_MELTING_THRESHOLD_SKY;
    public static ForgeConfigSpec.DoubleValue SNOW_TICK_CHANCE;
    public static ForgeConfigSpec.DoubleValue BIOME_TEMPERATURE_THRESHOLD;
    public static ForgeConfigSpec.DoubleValue SNOW_DOWNFALL;

    // toggles
    public static ForgeConfigSpec.BooleanValue ENTITY_AFFECT_SNOW;
    public static ForgeConfigSpec.BooleanValue ITEM_AFFECT_SNOW;
    public static ForgeConfigSpec.BooleanValue PRESERVE_FARMLAND;
    public static ForgeConfigSpec.BooleanValue SNOW_UNDER_TREE;
    public static ForgeConfigSpec.BooleanValue SNOW_BLOCKS_CAN_FALL;
    public static ForgeConfigSpec.BooleanValue WATER_CAN_FREEZE;

    public Configs(ForgeConfigSpec.Builder forgeConfigBuilder) {
        ConfigBuilder builder = new ConfigBuilder(forgeConfigBuilder);

        SNOW_LIGHT_MELTING_THRESHOLD_BLOCK = builder.defineRange("snow_light_melting_threshold_block", 11, -1, 15);
        SNOW_LIGHT_MELTING_THRESHOLD_SKY = builder.defineRange("snow_light_melting_threshold_sky", 11, -1, 15);
        MAX_SNOW_DEPTH = builder.defineRange("max_snow_depth", 3, 0, 256);
        SNOW_TICK_CHANCE = builder.defineRange("snow_tick_chance", 12.5, 1.0, 100.0);
        BIOME_TEMPERATURE_THRESHOLD = builder.defineRange("biome_temperature_threshold", 0.2, 0.0, 2.0);
        SNOW_DOWNFALL = builder.defineRange("snow_downfall", 1.0, 0.1, 1.0);

        ENTITY_AFFECT_SNOW = builder.defineBoolean("entity_affect_snow", true);
        ITEM_AFFECT_SNOW = builder.defineBoolean("entity_affect_snow", true);
        PRESERVE_FARMLAND = builder.defineBoolean("entity_affect_snow", true);
        SNOW_UNDER_TREE = builder.defineBoolean("entity_affect_snow", true);
        SNOW_BLOCKS_CAN_FALL = builder.defineBoolean("entity_affect_snow", true);
        WATER_CAN_FREEZE = builder.defineBoolean("entity_affect_snow", true);
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

        public <V extends Enum<V>> ForgeConfigSpec.EnumValue<V> defineEnum(String key, V defaultValue, V... enums) {
            return BUILDER.comment(fromConfigKey(key), noteFromConfigKey(key)).defineEnum(key, defaultValue, enums);
        }

        private static String fromConfigKey(String key) {
            return new TranslationTextComponent("config." + key).getUnformattedComponentText();
        }

        private static String noteFromConfigKey(String key) {
            return fromConfigKey("prefix.note") + ": " + fromConfigKey(key + ".note");
        }
    }

    static {
        Pair<Configs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Configs::new);
        CONFIG_SPEC = pair.getRight();
        CONFIGS = pair.getLeft();
    }
}
