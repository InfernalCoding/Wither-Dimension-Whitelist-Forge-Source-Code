package net.infernal_coding.wither_spawn_control;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public final class Config {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WITHER_SPAWNABLE_DIMENSIONS = BUILDER
            .comment("Define dimensions that the Wither can spawn in as a string in the form {modid:dimension_name}, " +
                    "\n with the dimensions being separated by quotation marks, then by a comma inside of the [] brackets")
            .defineList("Dimensions that the Wither can spawn in",
                    Arrays.asList("minecraft:the_nether", "minecraft:the_end", "minecraft:overworld"), dimension -> dimension instanceof String);

    public static final ForgeConfigSpec.BooleanValue SHOW_DIMENSIONS_TO_PLAYER = BUILDER
            .comment("Define whether or not the text the player receives when attempting to spawn the wither will show what dimensions the wither is spawnable in.")
            .define("Show valid dimensions:", true);

    static {
        SPEC = BUILDER.build();
    }


}
