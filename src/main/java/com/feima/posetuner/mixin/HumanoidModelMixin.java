package com.feima.posetuner.mixin;

import com.feima.posetuner.TunerConfig;
import com.feima.posetuner.client.ForcedPoseState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 强制姿态的各部位：头 / 躯干 / 双臂 / 双腿的局部变换。
 *
 * <p>每帧从 {@link TunerConfig} 直接读值，因此 Configured 改完
 * 立即生效。
 *
 * <p><b>每帧先恢复</b>：原版 {@code setupAnim} 只重置大部分 {@code xRot}
 * / {@code yRot} / {@code zRot}，不重置 {@code x / y / z}（位置）
 * 和 {@code head.zRot}。不改回来会导致姿态结束后残留。
 */
@Mixin(value = HumanoidModel.class, priority = 1400)
public abstract class HumanoidModelMixin {

    @Shadow public ModelPart head;
    @Shadow public ModelPart body;
    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftArm;
    @Shadow public ModelPart rightLeg;
    @Shadow public ModelPart leftLeg;

    private static final Map<ModelPart, float[]> PT$INIT = new IdentityHashMap<>();

    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At("TAIL")
    )
    private void pt$applyParts(LivingEntity entity, float limbSwing, float limbSwingAmount,
                               float ageInTicks, float netHeadYaw, float headPitch,
                               CallbackInfo ci) {
        // 1) 每帧恢复原版不重置的字段
        pt$restore(this.head);
        pt$restore(this.body);
        pt$restore(this.rightArm);
        pt$restore(this.leftArm);
        pt$restore(this.rightLeg);
        pt$restore(this.leftLeg);
        this.head.zRot = 0.0F;

        // 2) 只在强制姿态开启、本地玩家第三人称时应用
        if (!(entity instanceof Player)) return;
        if (!ForcedPoseState.isActive()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.player != entity) return;
        if (mc.options.getCameraType().isFirstPerson()) return;

        pt$apply(this.head,     TunerConfig.head);
        pt$apply(this.body,     TunerConfig.body);
        pt$apply(this.rightArm, TunerConfig.rightArm);
        pt$apply(this.leftArm,  TunerConfig.leftArm);
        pt$apply(this.rightLeg, TunerConfig.rightLeg);
        pt$apply(this.leftLeg,  TunerConfig.leftLeg);
    }

    private static void pt$restore(ModelPart part) {
        float[] init = PT$INIT.get(part);
        if (init == null) {
            init = new float[]{part.x, part.y, part.z};
            PT$INIT.put(part, init);
        }
        part.x = init[0];
        part.y = init[1];
        part.z = init[2];
    }

    private static void pt$apply(ModelPart part, TunerConfig.Fields f) {
        part.xRot = (float) Math.toRadians(f.rx.get());
        part.yRot = (float) Math.toRadians(f.ry.get());
        part.zRot = (float) Math.toRadians(f.rz.get());

        float[] init = PT$INIT.get(part);
        // pt$restore 已保证 init 存在
        part.x = init[0] + f.px.get().floatValue();
        part.y = init[1] + f.py.get().floatValue();
        part.z = init[2] + f.pz.get().floatValue();
    }
}