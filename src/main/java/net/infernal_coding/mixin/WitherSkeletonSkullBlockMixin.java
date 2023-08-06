package net.infernal_coding.mixin;

import com.google.common.collect.Lists;
import net.infernal_coding.config.ModConfig;
import net.minecraft.block.WitherSkeletonSkullBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(WitherSkeletonSkullBlock.class)
public class WitherSkeletonSkullBlockMixin {

    @Redirect(
            method = "checkSpawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/pattern/BlockPattern;find(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/pattern/BlockPattern$PatternHelper;")
    )

    /*Checks if the wither is spawned correctly and is a valid dimension from the wither_spawn_control.toml file*/
    private static BlockPattern.PatternHelper checkSpawn$preventForDim(BlockPattern blockPattern, IWorldReader worldReader, BlockPos pos) {
        World world = (World) worldReader;

        BlockPattern.PatternHelper structure = blockPattern.find(world, pos);
        if (structure == null) {
            return null;
        }

        for (String dimension : ModConfig.WITHER_SPAWNABLE_DIMENSIONS.get()) {
            if (world.dimension().location().toString().equals(dimension)) {
                return structure;
            }
        }

        List<PlayerEntity> players = getNearbyPlayers(world, new AxisAlignedBB(pos).inflate(16));
        StringBuilder dimensionList;

        if (ModConfig.SHOW_DIMENSIONS_TO_PLAYER.get()) {

            if (ModConfig.WITHER_SPAWNABLE_DIMENSIONS.get().isEmpty()) {
                dimensionList = new StringBuilder(new TranslationTextComponent("text.wither_dimension_whitelist.cannot_spawn_wither_anywhere").getString());
            } else {

                dimensionList = new StringBuilder(new TranslationTextComponent("text.wither_dimension_whitelist.cannot_spawn_wither_here_with_dimensions").getString());

                int i = 0;

                for (String dimension : ModConfig.WITHER_SPAWNABLE_DIMENSIONS.get()) {

                    String dimensionName = getFormattedDimensionName(dimension);

                    if (i + 1 == ModConfig.WITHER_SPAWNABLE_DIMENSIONS.get().size()) {
                        if (i == 0) {
                            dimensionList.append(dimensionName).append(".");
                        } else {
                            dimensionList.append("and ").append(dimensionName).append(".");
                        }
                    } else {
                        dimensionList.append(dimensionName).append(", ");
                    }
                    i++;
                }
            }
        } else {
            dimensionList = new StringBuilder(new TranslationTextComponent("text.wither_dimension_whitelist.cannot_spawn_wither_here").getString());
        }

        for (PlayerEntity player : players) {
            player.displayClientMessage(new StringTextComponent(dimensionList.toString()), true);
        }
        return null;
    }

    private static String getFormattedDimensionName(String dimension) {
        String dimensionName = new ResourceLocation(dimension).getPath();
        String[] parts = dimensionName.split("_");

        //If the dimensionName can be represented as an array split by "_", add "the" to the first index of the array if not in the String. Then, create
        //an uppercase dimension name from the array
        if (parts.length > 1) {
                if (!parts[0].startsWith("the")) {
                    try {
                        parts[0] = "the " + parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1);
                    } catch (Exception e) {
                        parts[0] = "the " + parts[0].substring(0, 1).toUpperCase();
                    }
                }

                int i = 0;
            for (String part : parts) {
                if (i == 0) {
                    dimensionName = part + " ";
                } else {
                    try {
                        if (i + 1 == parts.length) {
                            dimensionName += part.substring(0, 1).toUpperCase() + part.substring(1);
                        } else
                            dimensionName += part.substring(0, 1).toUpperCase() + part.substring(1) + " ";
                    } catch (Exception e) {
                        if (i + 1 == parts.length) {
                            dimensionName += part.substring(0, 1).toUpperCase();
                        }
                        dimensionName += part.substring(0, 1).toUpperCase() + " ";
                    }
                }
                i++;
            }

            //If the dimensionName can't be represented as an array split by "_", add "the" to the String if not at the start. Then, create
            //an uppercase dimension name from the String
        } else {
            if (!dimensionName.startsWith("the")) {
                try {
                    dimensionName = "the " + dimensionName.substring(0, 1).toUpperCase() + dimensionName.substring(1);
                } catch (Exception e) {
                    dimensionName = "the " + dimensionName.substring(0, 1).toUpperCase();
                }
            }
        }
        return dimensionName;
    }

    private static List<PlayerEntity> getNearbyPlayers(World world, AxisAlignedBB bb) {
        List<PlayerEntity> list = Lists.newArrayList();

        for (PlayerEntity playerentity : world.players()) {
            if (bb.contains(playerentity.getX(), playerentity.getY(), playerentity.getZ())) {
                list.add(playerentity);
            }
        }
        return list;
    }

}
