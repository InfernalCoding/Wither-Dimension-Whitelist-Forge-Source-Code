package net.infernal_coding.wither_spawn_control.mixins;

import com.google.common.collect.Lists;
import net.infernal_coding.wither_spawn_control.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(WitherSkullBlock.class)
public class WitherSkeletonSkullBlockMixin {

    @Redirect(
            method = "checkSpawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/pattern/BlockPattern;find(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/pattern/BlockPattern$BlockPatternMatch;")
    )

    /*Checks if the wither is spawned correctly and is a valid dimension from the wither_spawn_control.toml file*/
    private static BlockPattern.BlockPatternMatch checkSpawn$preventForDim(BlockPattern blockPattern, LevelReader reader, BlockPos pos) {
        Level world = (Level) reader;

        BlockPattern.BlockPatternMatch structure = blockPattern.find(world, pos);
        if (structure == null) {
            return null;
        }

        for (String dimension : Config.WITHER_SPAWNABLE_DIMENSIONS.get()) {
            if (world.dimension().location().toString().equals(dimension)) {
                return structure;
            }
        }

        List<Player> players = getNearbyPlayers(world, new AABB(pos).inflate(16));
        StringBuilder dimensionList;

        if (Config.SHOW_DIMENSIONS_TO_PLAYER.get()) {

            if (Config.WITHER_SPAWNABLE_DIMENSIONS.get().isEmpty()) {
                dimensionList = new StringBuilder(new TranslatableComponent("text.wither_spawn_control.cannot_spawn_wither_anywhere").getString());
            } else {

                dimensionList = new StringBuilder(new TranslatableComponent("text.wither_spawn_control.cannot_spawn_wither_here_with_dimensions").getString());

                int i = 0;

                for (String dimension : Config.WITHER_SPAWNABLE_DIMENSIONS.get()) {

                    String dimensionName = getFormattedDimensionName(dimension);

                    if (i + 1 == Config.WITHER_SPAWNABLE_DIMENSIONS.get().size()) {
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
            dimensionList = new StringBuilder(new TranslatableComponent("text.wither_spawn_control.cannot_spawn_wither_here").getString());
        }

        for (Player player : players) {
            player.displayClientMessage(new TextComponent(dimensionList.toString()), true);
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

    private static List<Player> getNearbyPlayers(Level world, AABB bb) {
        List<Player> list = Lists.newArrayList();

        for (Player playerentity : world.players()) {
            if (bb.contains(playerentity.getX(), playerentity.getY(), playerentity.getZ())) {
                list.add(playerentity);
            }
        }
        return list;
    }

}
