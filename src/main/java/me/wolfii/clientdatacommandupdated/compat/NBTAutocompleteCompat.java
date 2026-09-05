package me.wolfii.clientdatacommandupdated.compat;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.mt1006.nbtac.api.NBTacAPI;

import java.util.concurrent.CompletableFuture;

public class NBTAutocompleteCompat {
    private NBTAutocompleteCompat() {
    }

    public static CompletableFuture<Suggestions> suggestEntityPath(String remaining, EntityType<?> type, SuggestionsBuilder builder) {
        return NBTacAPI.getNbtSuggestions(remaining, "entity/" + BuiltInRegistries.ENTITY_TYPE.getKey(type), builder, true, null);
    }

    public static CompletableFuture<Suggestions> suggestBlockPath(String remaining, Block block, SuggestionsBuilder builder) {
        return NBTacAPI.getNbtSuggestions(remaining, "block/" + BuiltInRegistries.BLOCK.getKey(block), builder, true, null);
    }
}
