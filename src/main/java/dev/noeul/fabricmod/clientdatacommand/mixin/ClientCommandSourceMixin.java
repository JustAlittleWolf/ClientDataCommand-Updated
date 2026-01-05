package dev.noeul.fabricmod.clientdatacommand.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.permission.PermissionPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ClientCommandSource.class)
public class ClientCommandSourceMixin {
	@Inject(method = "getPermissions", at = @At("HEAD"), cancellable = true)
	private void inject$hasPermissionLevel(CallbackInfoReturnable<PermissionPredicate> cir) {
        cir.setReturnValue(PermissionPredicate.ALL);
	}
}
