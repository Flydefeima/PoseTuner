package com.feima.posetuner.mixin;

import com.feima.posetuner.TunerConfig;
import com.feima.posetuner.client.ForcedPoseState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 强制姿态的 root 部分：整体刚体变换（PoseStack 层）。
 *
 * <p>变换顺序：先在模型空间平移（方向固定），再绕脚底 pivot 旋转。
 * 与 Blockbench 的骨骼变换顺序一致。
 *
 * <p>每帧从 {@link TunerConfig#root} 直接读值，因此 Configured
 * 改完立即生效。
 */
@Mixin(value = LivingEntityRenderer.class, priority = 1400)
public abstract class LivingEntityRendererMixin {

    private static final float FOOT_PIVOT_Y = 1.5F;

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FF" +
                     "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                     "Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;" +
                             "setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void pt$applyRoot(LivingEntity entity, float yaw, float partial,
                              PoseStack poseStack, MultiBufferSource buffer,
                              int light, CallbackInfo ci) {
        if (!pt$shouldApply(entity)) return;

        TunerConfig.Fields root = TunerConfig.root;
        if (root.isZero()) return;

        double rx = root.rx.get();
        double ry = root.ry.get();
        double rz = root.rz.get();
        double px = root.px.get();
        double py = root.py.get();
        double pz = root.pz.get();

        poseStack.pushPose();
        if (px != 0 || py != 0 || pz != 0) {
            poseStack.translate(px, py, pz);
        }
        poseStack.translate(0.0F, FOOT_PIVOT_Y, 0.0F);
        if (rx != 0) poseStack.mulPose(Axis.XP.rotationDegrees((float) rx));
        if (ry != 0) poseStack.mulPose(Axis.YP.rotationDegrees((float) ry));
        if (rz != 0) poseStack.mulPose(Axis.ZP.rotationDegrees((float) rz));
        poseStack.translate(0.0F, -FOOT_PIVOT_Y, 0.0F);
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FF" +
                     "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                     "Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN")
    )
    private void pt$popRoot(LivingEntity entity, float yaw, float partial,
                            PoseStack poseStack, MultiBufferSource buffer,
                            int light, CallbackInfo ci) {
        if (!pt$shouldApply(entity)) return;
        if (TunerConfig.root.isZero()) return;
        poseStack.popPose();
    }

    private static boolean pt$shouldApply(LivingEntity entity) {
        if (!(entity instanceof Player)) return false;
        if (!ForcedPoseState.isActive()) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        if (mc.player != entity) return false;
        if (mc.options.getCameraType().isFirstPerson()) return false;
        return true;
    }
}