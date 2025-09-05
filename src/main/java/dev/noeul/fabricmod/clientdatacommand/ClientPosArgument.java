package dev.noeul.fabricmod.clientdatacommand;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface ClientPosArgument extends PosArgument {
	default BlockPos toAbsoluteBlockPos(FabricClientCommandSource source) {
		return BlockPos.ofFloored(this.clientDataCommand$toAbsolutePos(source));
	}

	Vec3d clientDataCommand$toAbsolutePos(FabricClientCommandSource source);
}
