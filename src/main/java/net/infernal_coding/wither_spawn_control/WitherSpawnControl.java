package net.infernal_coding.wither_spawn_control;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WitherSpawnControl.MODID)
public class WitherSpawnControl
{
    public static final String MODID = "wither_spawn_control";

    public WitherSpawnControl() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
                Config.SPEC, "wither_dimension_whitelist-common.toml");
    }


}
