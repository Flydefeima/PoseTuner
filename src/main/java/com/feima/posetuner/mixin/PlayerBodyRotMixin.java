package com.feima.posetuner.mixin;

import com.feima.posetuner.client.ForcedPoseState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 强制姿态预览时，锁定本地玩家的身体朝向。
 *
 * <p>原版玩家在转动视角后 {@code yBodyRot} 会跟随 {@code yHeadRot}
 * 缓慢转向。预览时如果身体朝向一直变，模型角度看起来就不稳定。
 * 本 mixin 在 {@code Player.tick} 结束时把 {@code yBodyRot} 强制
 * 恢复到固定的正北方向，让身体保持不动、头仍可自由旋转。
 *
 * <p>只在强制姿态开启、且为本地玩家时生效。预览关闭后恢复原版行为。
 */
@Mixin(Player.class)
public abstract class PlayerBodyRotMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void pt$lockBodyRot(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!ForcedPoseState.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != self) return;

        float locked = ForcedPoseState.getLockedYaw();
        self.yBodyRot = locked;
        self.yBodyRotO = locked;
    }
}