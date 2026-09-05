package me.wolfii.clientdatacommandupdated;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.server.commands.data.BlockDataAccessor;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientDataCommandUpdated implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, _) ->
            dispatcher.register(
                ClientCommands.literal("datac").then(
                    ClientCommands.literal("get").then(
                        ClientCommands.literal("entity").then(
                            ClientCommands.argument("target", ClientEntityArgument.entity()).executes(context -> {
                                    Entity entity = ClientEntityArgument.getEntity(context, "target");
                                    EntityDataAccessor accessor = new EntityDataAccessor(entity);
                                    context.getSource().sendFeedback(accessor.getPrintSuccess(accessor.getData()));
                                    return Command.SINGLE_SUCCESS;
                                }
                            ).then(ClientCommands.argument("path", NbtPathArgument.nbtPath()).executes(context -> {
                                Entity entity = ClientEntityArgument.getEntity(context, "target");
                                NbtPathArgument.NbtPath path = context.getArgument("path", NbtPathArgument.NbtPath.class);
                                EntityDataAccessor accessor = new EntityDataAccessor(entity);
                                for (Tag tag : path.get(accessor.getData())) {
                                    context.getSource().sendFeedback(accessor.getPrintSuccess(tag));
                                }
                                return Command.SINGLE_SUCCESS;
                            }))
                        )
                    ).then(
                        ClientCommands.literal("block").then(
                            ClientCommands.argument("target", ClientBlockEntityPosArgument.blockEntityPos()).executes(context -> {
                                    BlockPos blockPos = ClientBlockEntityPosArgument.getLoadedBlockPos(context, "target");
                                    BlockEntity blockEntity = ClientBlockEntityPosArgument.getLoadedBlockEntity(context, "target");
                                    BlockDataAccessor accessor = new BlockDataAccessor(blockEntity, blockPos);
                                    context.getSource().sendFeedback(accessor.getPrintSuccess(accessor.getData()));
                                    return Command.SINGLE_SUCCESS;
                                }
                            ).then(ClientCommands.argument("path", NbtPathArgument.nbtPath()).executes(context -> {
                                BlockPos blockPos = ClientBlockEntityPosArgument.getLoadedBlockPos(context, "target");
                                BlockEntity blockEntity = ClientBlockEntityPosArgument.getLoadedBlockEntity(context, "target");
                                NbtPathArgument.NbtPath path = context.getArgument("path", NbtPathArgument.NbtPath.class);
                                BlockDataAccessor accessor = new BlockDataAccessor(blockEntity, blockPos);
                                for (Tag tag : path.get(accessor.getData())) {
                                    context.getSource().sendFeedback(accessor.getPrintSuccess(tag));
                                }
                                return Command.SINGLE_SUCCESS;
                            }))
                        )
                    )
                )
            )
        );
    }
}