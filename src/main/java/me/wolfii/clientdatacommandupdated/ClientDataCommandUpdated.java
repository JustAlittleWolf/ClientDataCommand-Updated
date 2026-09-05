package me.wolfii.clientdatacommandupdated;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.wolfii.clientdatacommandupdated.compat.NBTAutocompleteCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.server.commands.data.BlockDataAccessor;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientDataCommandUpdated implements ClientModInitializer {

    private static final boolean NBTAC_LOADED = FabricLoader.getInstance().isModLoaded("nbtac");

    private static ArgumentBuilder<FabricClientCommandSource, ?> dataGetCommandWithTargetAndSuggestions(
        String name,
        ArgumentBuilder<FabricClientCommandSource, ?> argument,
        DataAccessorProvider dataAccessorProvider,
        SuggestionProvider<FabricClientCommandSource> suggestionProvider
    ) {
        return ClientCommands.literal(name).then(
            argument
                .executes(context -> {
                    DataAccessor accessor = dataAccessorProvider.get(context);
                    context.getSource().sendFeedback(accessor.getPrintSuccess(accessor.getData()));
                    return Command.SINGLE_SUCCESS;
                })
                .then(ClientCommands.argument("path", NbtPathArgument.nbtPath())
                    .suggests(suggestionProvider)
                    .executes(context -> {
                        DataAccessor accessor = dataAccessorProvider.get(context);
                        NbtPathArgument.NbtPath path = context.getArgument("path", NbtPathArgument.NbtPath.class);
                        for (Tag tag : path.get(accessor.getData())) {
                            context.getSource().sendFeedback(accessor.getPrintSuccess(tag));
                        }
                        return Command.SINGLE_SUCCESS;
                    })
                )
        );
    }

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, _) ->
            dispatcher.register(
                ClientCommands.literal("datac").then(
                    ClientCommands.literal("get").then(
                        dataGetCommandWithTargetAndSuggestions(
                            "entity",
                            ClientCommands.argument("target", ClientEntityArgument.entity()),
                            context -> {
                                Entity entity = ClientEntityArgument.getEntity(context, "target");
                                return new EntityDataAccessor(entity);
                            },
                            (context, builder) -> {
                                if (!NBTAC_LOADED) return builder.buildFuture();
                                Entity entity = ClientEntityArgument.getEntity(context, "target");
                                return NBTAutocompleteCompat.suggestEntityPath(builder.getRemaining(), entity.getType(), builder);
                            }
                        )
                    ).then(dataGetCommandWithTargetAndSuggestions(
                        "block",
                        ClientCommands.argument("targetPos", ClientBlockEntityPosArgument.blockEntityPos()),
                        context -> {
                            BlockPos blockPos = ClientBlockEntityPosArgument.getLoadedBlockPos(context, "targetPos");
                            BlockEntity blockEntity = ClientBlockEntityPosArgument.getLoadedBlockEntity(context, "targetPos");
                            return new BlockDataAccessor(blockEntity, blockPos);
                        },
                        (context, builder) -> {
                            if (!NBTAC_LOADED) return builder.buildFuture();
                            Block block = ClientBlockEntityPosArgument.getLoadedBlockEntity(context, "targetPos").getBlockState().getBlock();
                            return NBTAutocompleteCompat.suggestBlockPath(builder.getRemaining(), block, builder);
                        }
                    ))
                )
            )
        );
    }

    @FunctionalInterface
    private interface DataAccessorProvider {
        DataAccessor get(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException;
    }
}
