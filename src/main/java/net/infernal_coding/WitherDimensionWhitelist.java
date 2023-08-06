package net.infernal_coding;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(WitherDimensionWhitelist.MOD_ID)
public class WitherDimensionWhitelist {
    // Directly reference a log4j logger.

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "wither_dimension_whitelist";
    
    public WitherDimensionWhitelist() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
                net.infernal_coding.config.ModConfig.SPEC,
                "wither_dimension_whitelist-common.toml");
    }
}
