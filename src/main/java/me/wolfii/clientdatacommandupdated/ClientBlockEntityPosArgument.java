package me.wolfii.clientdatacommandupdated;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ClientBlockEntityPosArgument {
    SimpleCommandExceptionType ERROR_NOT_BLOCK_ENTITY = new SimpleCommandExceptionType(Component.literal("Block is not block-entity"));

    static BlockPosArgument blockEntityPos() {
        return BlockPosArgument.blockPos();
    }

    static BlockPos getLoadedBlockPos(CommandContext<FabricClientCommandSource> context, String name) throws CommandSyntaxException {
        BlockPos blockPos = context.getArgument("target", Coordinates.class).getBlockPos(new FabricClientCommandSourceStack(context.getSource()));
        Level level = context.getSource().getLevel();
        if (!level.hasChunk(SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getZ()))) {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        } else if (!level.isInWorldBounds(blockPos)) {
            throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();
        }
        return blockPos;
    }

    static BlockEntity getLoadedBlockEntity(CommandContext<FabricClientCommandSource> context, String name) throws CommandSyntaxException {
        BlockEntity blockEntity = context.getSource().getLevel().getBlockEntity(getLoadedBlockPos(context, name));
        if (blockEntity == null) {
            throw ERROR_NOT_BLOCK_ENTITY.create();
        }
        return blockEntity;
    }
}
