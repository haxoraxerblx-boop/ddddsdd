package com.customskin.mixin;

import com.customskin.SkinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.player.PlayerModelType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {

	@Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
	private void customskin$override(CallbackInfoReturnable<PlayerSkin> cir) {
		AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;
		Minecraft mc = Minecraft.getInstance();

		// Only ever touch the local player, never anyone else.
		if (mc.player == null || self != mc.player) return;

		SkinManager.refreshIfNeeded();
		if (!SkinManager.isLoaded()) return;

		PlayerSkin original = cir.getReturnValue();
		ClientAsset.ResourceTexture body = new ClientAsset.ResourceTexture(SkinManager.textureId(), SkinManager.textureId());
		cir.setReturnValue(new PlayerSkin(
				body,
				original.cape(),
				original.elytra(),
				SkinManager.isSlim() ? PlayerModelType.SLIM : PlayerModelType.WIDE,
				original.secure()
		));
	}
}
